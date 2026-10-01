package com.example.data.supabase

import android.util.Log
import com.example.data.model.MosqueSettings
import com.example.data.model.MosqueSubscriber
import com.example.data.model.MosqueSupportTicket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseService(private val config: SupabaseConfig) {

  companion object {
    private const val TAG = "SupabaseService"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
  }

  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

  /**
   * Menguji konektivitas ke database Supabase.
   */
  suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) {
      return@withContext Result.failure(IllegalStateException("Supabase URL atau Anon Key belum diisi."))
    }

    try {
      val startTime = System.currentTimeMillis()
      val url = "${config.supabaseUrl}/rest/v1/mosques?select=count&limit=1"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .addHeader("Range-Unit", "items")
        .addHeader("Prefer", "count=exact")
        .get()
        .build()

      val response = client.newCall(request).execute()
      val latency = System.currentTimeMillis() - startTime

      if (response.isSuccessful) {
        val contentRange = response.header("Content-Range") ?: "0"
        return@withContext Result.success("Sukses terhubung (${latency}ms). Header: $contentRange")
      } else {
        val errorBody = response.body?.string() ?: "HTTP ${response.code}"
        return@withContext Result.failure(Exception("Gagal: HTTP ${response.code} - $errorBody"))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Test connection error", e)
      return@withContext Result.failure(e)
    }
  }

  /**
   * Sinkronisasi data masjid & saldo kas ke Supabase (Upsert berbasis device_id).
   */
  suspend fun upsertMosque(
    subscriber: MosqueSubscriber,
    settings: MosqueSettings
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) {
      return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))
    }

    try {
      val json = JSONObject().apply {
        put("id", subscriber.id.ifBlank { "MOSQUE-${subscriber.cityName.uppercase()}-${subscriber.activeDeviceId.takeLast(6)}" })
        put("device_id", subscriber.activeDeviceId)
        put("mosque_name", settings.mosqueName.ifBlank { subscriber.mosqueName })
        put("mosque_address", settings.mosqueAddress.ifBlank { subscriber.mosqueAddress })
        put("city_name", settings.cityName.ifBlank { subscriber.cityName })
        put("latitude", settings.latitude)
        put("longitude", settings.longitude)
        put("dkm_leader_name", subscriber.dkmLeaderName)
        put("contact_phone", subscriber.contactPhone)
        put("contact_email", subscriber.contactEmail)
        put("device_model", subscriber.deviceModel)
        put("is_pro", subscriber.isPro)
        put("subscription_type", subscriber.subscriptionType)
        put("registered_date", subscriber.registeredDate)
        put("expiry_date", subscriber.expiryDate)
        put("last_active_date", subscriber.lastActiveDate)
        put("order_id", subscriber.orderId)
        put("kas_saldo", settings.kasSaldo)
        put("kas_pemasukan", settings.kasPemasukan)
        put("kas_pengeluaran", settings.kasPengeluaran)
        put("running_text", settings.runningTexts.joinToString(" • "))
        put("jumat_khotib", settings.jumatKhotib)
        put("jumat_imam", settings.jumatImam)
        put("jumat_muadzin", settings.jumatMuadzin)
      }

      val url = "${config.supabaseUrl}/rest/v1/mosques?on_conflict=device_id"
      val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .addHeader("Prefer", "resolution=merge-duplicates,return=representation")
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        config.lastSyncTimestamp = System.currentTimeMillis()
        Log.d(TAG, "Berhasil sinkronisasi ke Supabase: ${subscriber.mosqueName}")
        return@withContext Result.success(true)
      } else {
        val error = response.body?.string() ?: "HTTP ${response.code}"
        Log.w(TAG, "Gagal upsert ke Supabase: $error")
        return@withContext Result.failure(Exception("Supabase HTTP ${response.code}: $error"))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error upserting mosque to Supabase", e)
      return@withContext Result.failure(e)
    }
  }

  /**
   * Mengambil semua daftar masjid pelanggan yang tersimpan di Supabase
   * untuk ditampilkan di panel Superadmin.
   */
  suspend fun fetchAllMosques(): Result<List<MosqueSubscriber>> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) {
      return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))
    }

    try {
      val url = "${config.supabaseUrl}/rest/v1/mosques?select=*&order=updated_at.desc"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .get()
        .build()

      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        val error = response.body?.string() ?: "HTTP ${response.code}"
        return@withContext Result.failure(Exception("Gagal mengambil data dari Supabase: $error"))
      }

      val responseBody = response.body?.string() ?: "[]"
      val jsonArray = JSONArray(responseBody)
      val list = mutableListOf<MosqueSubscriber>()

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          MosqueSubscriber(
            id = obj.optString("id", "MOSQUE-$i"),
            mosqueName = obj.optString("mosque_name", "Masjid"),
            cityName = obj.optString("city_name", ""),
            mosqueAddress = obj.optString("mosque_address", ""),
            latitude = obj.optDouble("latitude", 0.0),
            longitude = obj.optDouble("longitude", 0.0),
            dkmLeaderName = obj.optString("dkm_leader_name", ""),
            contactEmail = obj.optString("contact_email", ""),
            contactPhone = obj.optString("contact_phone", ""),
            activeDeviceId = obj.optString("device_id", ""),
            deviceModel = obj.optString("device_model", ""),
            isPro = obj.optBoolean("is_pro", false),
            subscriptionType = obj.optString("subscription_type", "Langganan Bulanan"),
            registeredDate = obj.optString("registered_date", ""),
            expiryDate = obj.optString("expiry_date", ""),
            lastActiveDate = obj.optString("last_active_date", ""),
            orderId = obj.optString("order_id", "")
          )
        )
      }

      config.lastSyncTimestamp = System.currentTimeMillis()
      return@withContext Result.success(list)
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching mosques from Supabase", e)
      return@withContext Result.failure(e)
    }
  }

  /**
   * Superadmin: Mengubah status langganan masjid di Supabase.
   */
  suspend fun updateSubscription(
    deviceId: String,
    isPro: Boolean,
    expiryDate: String,
    subscriptionType: String
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))

    try {
      val json = JSONObject().apply {
        put("is_pro", isPro)
        put("expiry_date", expiryDate)
        put("subscription_type", subscriptionType)
      }

      val url = "${config.supabaseUrl}/rest/v1/mosques?device_id=eq.$deviceId"
      val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .patch(requestBody)
        .build()

      val response = client.newCall(request).execute()
      return@withContext if (response.isSuccessful) Result.success(true)
      else Result.failure(Exception("Gagal: HTTP ${response.code}"))
    } catch (e: Exception) {
      return@withContext Result.failure(e)
    }
  }

  /**
   * Superadmin: Reset ikatan perangkat agar masjid bisa login di TV/HP baru.
   */
  suspend fun resetDeviceBinding(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))

    try {
      val json = JSONObject().apply {
        put("device_id", "")
        put("device_model", "(Belum terikat perangkat)")
      }

      val url = "${config.supabaseUrl}/rest/v1/mosques?id=eq.$id"
      val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .patch(requestBody)
        .build()

      val response = client.newCall(request).execute()
      return@withContext if (response.isSuccessful) Result.success(true)
      else Result.failure(Exception("Gagal: HTTP ${response.code}"))
    } catch (e: Exception) {
      return@withContext Result.failure(e)
    }
  }

  /**
   * Superadmin: Kirim pesan broadcast ke layar masjid tertentu.
   */
  suspend fun sendBroadcastMessage(deviceId: String, message: String): Result<Boolean> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))

    try {
      val json = JSONObject().apply {
        put("broadcast_message", message)
      }

      val url = "${config.supabaseUrl}/rest/v1/mosques?device_id=eq.$deviceId"
      val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .patch(requestBody)
        .build()

      val response = client.newCall(request).execute()
      return@withContext if (response.isSuccessful) Result.success(true)
      else Result.failure(Exception("Gagal: HTTP ${response.code}"))
    } catch (e: Exception) {
      return@withContext Result.failure(e)
    }
  }

  /**
   * Mengambil tiket bantuan & komplain dari Supabase.
   */
  suspend fun fetchTickets(): Result<List<MosqueSupportTicket>> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))

    try {
      val url = "${config.supabaseUrl}/rest/v1/mosque_tickets?select=*&order=created_at_timestamp.desc"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .get()
        .build()

      val response = client.newCall(request).execute()
      if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))

      val body = response.body?.string() ?: "[]"
      val arr = JSONArray(body)
      val list = mutableListOf<MosqueSupportTicket>()

      for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        list.add(
          MosqueSupportTicket(
            id = o.optString("id", ""),
            mosqueName = o.optString("mosque_name", ""),
            cityName = o.optString("city_name", ""),
            senderContact = o.optString("sender_contact", o.optString("contact_info", "")),
            category = o.optString("category", "Bantuan Umum"),
            issueMessage = o.optString("issue_message", o.optString("description", "")),
            deviceId = o.optString("device_id", ""),
            deviceModel = o.optString("device_model", ""),
            createdAt = o.optString("created_at", ""),
            isResolved = o.optBoolean("is_resolved", false)
          )
        )
      }
      return@withContext Result.success(list)
    } catch (e: Exception) {
      return@withContext Result.failure(e)
    }
  }

  /**
   * Tandai tiket selesai di Supabase.
   */
  suspend fun updateTicketStatus(ticketId: String, isResolved: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.failure(IllegalStateException("Supabase belum dikonfigurasi"))

    try {
      val json = JSONObject().apply {
        put("is_resolved", isResolved)
      }

      val url = "${config.supabaseUrl}/rest/v1/mosque_tickets?id=eq.$ticketId"
      val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.supabaseAnonKey)
        .addHeader("Authorization", "Bearer ${config.supabaseAnonKey}")
        .patch(requestBody)
        .build()

      val response = client.newCall(request).execute()
      return@withContext if (response.isSuccessful) Result.success(true)
      else Result.failure(Exception("HTTP ${response.code}"))
    } catch (e: Exception) {
      return@withContext Result.failure(e)
    }
  }
}
