package com.example.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.data.model.MosqueSettings
import com.example.data.model.MosqueSubscriber
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class MosqueSubscriberRepository(private val context: Context) {

  companion object {
    private const val TAG = "MosqueSubscriberRepo"
    private const val PREFS_NAME = "mosque_subscriber_prefs"
    private const val KEY_DEVICE_ID = "device_hardware_id"
    private const val KEY_ADMIN_PIN = "admin_security_pin"
    private const val DEFAULT_ADMIN_PIN = "192837"
    private const val KEY_LOCAL_SUBSCRIBERS_JSON = "local_subscribers_json"
    private const val KEY_CURRENT_MOSQUE_ID = "current_registered_mosque_id"
    private const val FIRESTORE_COLLECTION = "mosque_subscribers"
  }

  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _subscribersList = MutableStateFlow<List<MosqueSubscriber>>(loadLocalSubscribers())
  val subscribersList: StateFlow<List<MosqueSubscriber>> = _subscribersList.asStateFlow()

  val deviceId: String by lazy { getOrCreateDeviceId() }
  val deviceModel: String by lazy { "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}" }

  @SuppressLint("HardwareIds")
  private fun getOrCreateDeviceId(): String {
    val saved = prefs.getString(KEY_DEVICE_ID, null)
    if (!saved.isNullOrBlank()) return saved

    val androidId = try {
      Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    } catch (_: Exception) {
      null
    }

    val generated = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
      "DEV-$androidId"
    } else {
      "DEV-${UUID.randomUUID().toString().take(12)}"
    }

    prefs.edit().putString(KEY_DEVICE_ID, generated).apply()
    return generated
  }

  fun getAdminPin(): String {
    return prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN
  }

  fun verifyAdminPin(inputPin: String): Boolean {
    return inputPin.trim() == getAdminPin()
  }

  fun updateAdminPin(newPin: String) {
    prefs.edit().putString(KEY_ADMIN_PIN, newPin.trim()).apply()
  }

  /**
   * Mengirim / memperbarui data masjid ke Firebase Firestore saat berlangganan.
   * Mengikat lisensi ke perangkat ini (Single Device Policy).
   */
  suspend fun recordOrUpdateSubscription(
    settings: MosqueSettings,
    orderId: String = "",
    contactPhone: String = "",
    contactEmail: String = ""
  ): MosqueSubscriber = withContext(Dispatchers.IO) {
    val now = Calendar.getInstance()
    val dateFmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val regDate = dateFmt.format(now.time) + " WIB"

    // Default expiry 1 month from now
    val expiryCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 31) }
    val expDate = dateFmt.format(expiryCal.time) + " WIB"

    var existingId = prefs.getString(KEY_CURRENT_MOSQUE_ID, null)
    if (existingId.isNullOrBlank()) {
      existingId = "MOSQUE-${settings.cityName.uppercase().replace(" ", "_")}-${deviceId.takeLast(6)}"
      prefs.edit().putString(KEY_CURRENT_MOSQUE_ID, existingId).apply()
    }

    val subscriber = MosqueSubscriber(
      id = existingId,
      mosqueName = settings.mosqueName,
      cityName = settings.cityName,
      mosqueAddress = settings.mosqueAddress,
      latitude = settings.latitude,
      longitude = settings.longitude,
      contactEmail = contactEmail.ifBlank { "dkm@${settings.cityName.lowercase().replace(" ", "")}.org" },
      contactPhone = contactPhone,
      activeDeviceId = deviceId,
      deviceModel = deviceModel,
      isPro = true,
      subscriptionType = "Google Play (Rp 10.000/bln)",
      registeredDate = regDate,
      expiryDate = expDate,
      lastActiveDate = regDate,
      orderId = orderId.ifBlank { "GPA.${System.currentTimeMillis()}" }
    )

    // Simpan ke local cache
    saveSubscriberLocally(subscriber)

    // Sinkronisasi ke Cloud Firestore
    syncToFirestore(subscriber)

    return@withContext subscriber
  }

  /**
   * Cek apakah perangkat ini cocok dengan perangkat yang terikat ke akun masjid.
   * Return true jika cocok atau belum terikat (aman).
   */
  fun checkDeviceBinding(subscriber: MosqueSubscriber): Boolean {
    if (subscriber.activeDeviceId.isBlank()) return true
    return subscriber.activeDeviceId == deviceId
  }

  /**
   * Simpan ke Firestore jika Firebase aktif.
   */
  private suspend fun syncToFirestore(subscriber: MosqueSubscriber) = withContext(Dispatchers.IO) {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        val db = FirebaseFirestore.getInstance()
        val dataMap = hashMapOf(
          "id" to subscriber.id,
          "mosqueName" to subscriber.mosqueName,
          "cityName" to subscriber.cityName,
          "mosqueAddress" to subscriber.mosqueAddress,
          "latitude" to subscriber.latitude,
          "longitude" to subscriber.longitude,
          "contactEmail" to subscriber.contactEmail,
          "contactPhone" to subscriber.contactPhone,
          "activeDeviceId" to subscriber.activeDeviceId,
          "deviceModel" to subscriber.deviceModel,
          "isPro" to subscriber.isPro,
          "subscriptionType" to subscriber.subscriptionType,
          "registeredDate" to subscriber.registeredDate,
          "expiryDate" to subscriber.expiryDate,
          "lastActiveDate" to subscriber.lastActiveDate,
          "orderId" to subscriber.orderId
        )

        db.collection(FIRESTORE_COLLECTION)
          .document(subscriber.id)
          .set(dataMap, SetOptions.merge())
          .await()

        Log.d(TAG, "Data masjid berhasil disimpan ke Firebase Firestore: ${subscriber.id}")
      }
    } catch (e: Exception) {
      Log.w(TAG, "Firestore sync note (akan tersimpan lokal jika google-services belum dipasang): ${e.message}")
    }
  }

  /**
   * Muat data seluruh masjid pelanggan dari Cloud Firestore / Local.
   */
  suspend fun fetchAllSubscribers(): List<MosqueSubscriber> = withContext(Dispatchers.IO) {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection(FIRESTORE_COLLECTION).get().await()
        val list = mutableListOf<MosqueSubscriber>()

        for (doc in snapshot.documents) {
          val sub = MosqueSubscriber(
            id = doc.getString("id") ?: doc.id,
            mosqueName = doc.getString("mosqueName") ?: "Masjid",
            cityName = doc.getString("cityName") ?: "",
            mosqueAddress = doc.getString("mosqueAddress") ?: "",
            latitude = doc.getDouble("latitude") ?: 0.0,
            longitude = doc.getDouble("longitude") ?: 0.0,
            contactEmail = doc.getString("contactEmail") ?: "",
            contactPhone = doc.getString("contactPhone") ?: "",
            activeDeviceId = doc.getString("activeDeviceId") ?: "",
            deviceModel = doc.getString("deviceModel") ?: "",
            isPro = doc.getBoolean("isPro") ?: false,
            subscriptionType = doc.getString("subscriptionType") ?: "Langganan Bulanan",
            registeredDate = doc.getString("registeredDate") ?: "",
            expiryDate = doc.getString("expiryDate") ?: "",
            lastActiveDate = doc.getString("lastActiveDate") ?: "",
            orderId = doc.getString("orderId") ?: ""
          )
          list.add(sub)
        }

        if (list.isNotEmpty()) {
          saveSubscribersListLocally(list)
          _subscribersList.value = list
          return@withContext list
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "Gagal fetch Firestore, menggunakan cache lokal: ${e.message}")
    }

    val local = loadLocalSubscribers()
    _subscribersList.value = local
    return@withContext local
  }

  /**
   * Fitur Admin: Reset ikatan perangkat (Device Binding)
   * Berguna jika pengurus masjid ganti HP atau TV baru.
   */
  suspend fun resetDeviceBinding(subscriberId: String): Boolean = withContext(Dispatchers.IO) {
    val current = _subscribersList.value.toMutableList()
    val index = current.indexOfFirst { it.id == subscriberId }
    if (index >= 0) {
      val updated = current[index].copy(activeDeviceId = "", deviceModel = "(Belum terikat perangkat)")
      current[index] = updated
      saveSubscribersListLocally(current)
      _subscribersList.value = current

      try {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
          FirebaseFirestore.getInstance().collection(FIRESTORE_COLLECTION)
            .document(subscriberId)
            .update(mapOf("activeDeviceId" to "", "deviceModel" to "(Belum terikat perangkat)"))
            .await()
        }
      } catch (_: Exception) {}

      return@withContext true
    }
    return@withContext false
  }

  /**
   * Fitur Admin: Toggle status langganan Pro secara manual
   */
  suspend fun toggleProStatus(subscriberId: String, isPro: Boolean): Boolean = withContext(Dispatchers.IO) {
    val current = _subscribersList.value.toMutableList()
    val index = current.indexOfFirst { it.id == subscriberId }
    if (index >= 0) {
      val updated = current[index].copy(isPro = isPro)
      current[index] = updated
      saveSubscribersListLocally(current)
      _subscribersList.value = current

      try {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
          FirebaseFirestore.getInstance().collection(FIRESTORE_COLLECTION)
            .document(subscriberId)
            .update("isPro", isPro)
            .await()
        }
      } catch (_: Exception) {}

      return@withContext true
    }
    return@withContext false
  }

  private fun saveSubscriberLocally(subscriber: MosqueSubscriber) {
    val currentList = loadLocalSubscribers().toMutableList()
    val idx = currentList.indexOfFirst { it.id == subscriber.id }
    if (idx >= 0) {
      currentList[idx] = subscriber
    } else {
      currentList.add(subscriber)
    }
    saveSubscribersListLocally(currentList)
    _subscribersList.value = currentList
  }

  private fun saveSubscribersListLocally(list: List<MosqueSubscriber>) {
    try {
      val array = JSONArray()
      for (sub in list) {
        val obj = JSONObject().apply {
          put("id", sub.id)
          put("mosqueName", sub.mosqueName)
          put("cityName", sub.cityName)
          put("mosqueAddress", sub.mosqueAddress)
          put("latitude", sub.latitude)
          put("longitude", sub.longitude)
          put("contactEmail", sub.contactEmail)
          put("contactPhone", sub.contactPhone)
          put("activeDeviceId", sub.activeDeviceId)
          put("deviceModel", sub.deviceModel)
          put("isPro", sub.isPro)
          put("subscriptionType", sub.subscriptionType)
          put("registeredDate", sub.registeredDate)
          put("expiryDate", sub.expiryDate)
          put("lastActiveDate", sub.lastActiveDate)
          put("orderId", sub.orderId)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_LOCAL_SUBSCRIBERS_JSON, array.toString()).apply()
    } catch (_: Exception) {}
  }

  private fun loadLocalSubscribers(): List<MosqueSubscriber> {
    val raw = prefs.getString(KEY_LOCAL_SUBSCRIBERS_JSON, null)
    if (raw.isNullOrBlank()) {
      // Default demo initial subscriber to display in Admin Panel immediately
      return listOf(
        MosqueSubscriber(
          id = "MOSQUE-KEDIRI-001",
          mosqueName = "Masjid Agung Al-Kautsar",
          cityName = "Kediri",
          mosqueAddress = "Jl. Hayam Wuruk No. 10, Kota Kediri, Jawa Timur",
          latitude = -7.8480,
          longitude = 112.0178,
          contactEmail = "dkm.alkautsar@gmail.com",
          contactPhone = "0812-3456-7890",
          activeDeviceId = getOrCreateDeviceId(),
          deviceModel = deviceModel,
          isPro = true,
          subscriptionType = "Google Play (Rp 10.000/bln)",
          registeredDate = "01 Okt 2026, 09:00 WIB",
          expiryDate = "01 Nov 2026, 09:00 WIB",
          lastActiveDate = "01 Okt 2026, 12:30 WIB",
          orderId = "GPA.3391-4820-9182"
        )
      )
    }

    val list = mutableListOf<MosqueSubscriber>()
    try {
      val array = JSONArray(raw)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          MosqueSubscriber(
            id = obj.optString("id", ""),
            mosqueName = obj.optString("mosqueName", ""),
            cityName = obj.optString("cityName", ""),
            mosqueAddress = obj.optString("mosqueAddress", ""),
            latitude = obj.optDouble("latitude", 0.0),
            longitude = obj.optDouble("longitude", 0.0),
            contactEmail = obj.optString("contactEmail", ""),
            contactPhone = obj.optString("contactPhone", ""),
            activeDeviceId = obj.optString("activeDeviceId", ""),
            deviceModel = obj.optString("deviceModel", ""),
            isPro = obj.optBoolean("isPro", false),
            subscriptionType = obj.optString("subscriptionType", "Langganan"),
            registeredDate = obj.optString("registeredDate", ""),
            expiryDate = obj.optString("expiryDate", ""),
            lastActiveDate = obj.optString("lastActiveDate", ""),
            orderId = obj.optString("orderId", "")
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }
}
