package com.example.data.prayer

import android.util.Log
import com.example.data.model.PrayerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class OnlinePrayerResult(
  val isSuccess: Boolean,
  val timings: Map<PrayerType, String> = emptyMap(),
  val dateFormatted: String = "",
  val dateKey: String = "",
  val sourceName: String = "Kementerian Agama RI (Method 20)",
  val errorMessage: String? = null
)

object OnlinePrayerService {

  private const val TAG = "OnlinePrayerService"

  private val client: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(15, TimeUnit.SECONDS)
      .followRedirects(true)
      .followSslRedirects(true)
      .build()
  }

  /**
   * Fetches official prayer times from Aladhan API using Method 20 (Kementerian Agama RI).
   * Fully automated: retrieves Subuh, Syuruq, Dhuha, Dzuhur, Ashar, Maghrib, Isya, and Imsak.
   */
  suspend fun fetchPrayerTimes(
    latitude: Double,
    longitude: Double,
    calendar: Calendar = Calendar.getInstance()
  ): OnlinePrayerResult = withContext(Dispatchers.IO) {
    val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val currentDayKey = dayKeyFormat.format(calendar.time)

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    val dateStr = dateFormat.format(calendar.time)

    // Method 20 is "Kementerian Agama Republik Indonesia" (Fajr: 20°, Isha: 18°)
    val urlWithDate = "https://api.aladhan.com/v1/timings/$dateStr?latitude=$latitude&longitude=$longitude&method=20"
    val urlFallback = "https://api.aladhan.com/v1/timings?latitude=$latitude&longitude=$longitude&method=20"

    // Try primary with specific date first, then fallback to current timings
    val urlsToTry = listOf(urlWithDate, urlFallback)
    var lastError: String? = null

    for (url in urlsToTry) {
      try {
        Log.d(TAG, "Requesting prayer times online from: $url")
        val request = Request.Builder()
          .url(url)
          .header("User-Agent", "JamMasjidDigital/1.0 (Android; id)")
          .header("Accept", "application/json")
          .build()

        client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) {
            lastError = "Server HTTP ${response.code}"
            return@use
          }

          val bodyString = response.body?.string() ?: return@use
          val json = JSONObject(bodyString)
          val code = json.optInt("code", 0)
          if (code != 200) {
            lastError = json.optString("status", "Status $code")
            return@use
          }

          val dataObj = json.getJSONObject("data")
          val timingsObj = dataObj.getJSONObject("timings")

          // Clean time string: "04:12 (WIB)" -> "04:12"
          fun cleanTime(raw: String): String {
            val trimmed = raw.trim()
            val match = Regex("""\b(\d{1,2}:\d{2})\b""").find(trimmed)
            return match?.value ?: trimmed.take(5)
          }

          val rawImsak = timingsObj.optString("Imsak", "")
          val rawFajr = timingsObj.optString("Fajr", "")
          val rawSunrise = timingsObj.optString("Sunrise", "")
          val rawDhuhr = timingsObj.optString("Dhuhr", "")
          val rawAsr = timingsObj.optString("Asr", "")
          val rawMaghrib = timingsObj.optString("Maghrib", "")
          val rawIsha = timingsObj.optString("Isha", "")

          val imsakClean = cleanTime(rawImsak)
          val subuhClean = cleanTime(rawFajr)
          val syuruqClean = cleanTime(rawSunrise)
          val dzuhurClean = cleanTime(rawDhuhr)
          val asharClean = cleanTime(rawAsr)
          val maghribClean = cleanTime(rawMaghrib)
          val isyaClean = cleanTime(rawIsha)

          // Dhuha is 25 minutes after sunrise (syuruq)
          val dhuhaClean = calculateDhuhaFromSunrise(syuruqClean)

          val timingsMap = mutableMapOf<PrayerType, String>()
          if (imsakClean.isNotBlank()) timingsMap[PrayerType.IMSAK] = imsakClean
          if (subuhClean.isNotBlank()) timingsMap[PrayerType.SUBUH] = subuhClean
          if (syuruqClean.isNotBlank()) timingsMap[PrayerType.TERBIT] = syuruqClean
          if (dhuhaClean.isNotBlank()) timingsMap[PrayerType.DHUHA] = dhuhaClean
          if (dzuhurClean.isNotBlank()) timingsMap[PrayerType.DZUHUR] = dzuhurClean
          if (asharClean.isNotBlank()) timingsMap[PrayerType.ASHAR] = asharClean
          if (maghribClean.isNotBlank()) timingsMap[PrayerType.MAGHRIB] = maghribClean
          if (isyaClean.isNotBlank()) timingsMap[PrayerType.ISYA] = isyaClean

          val readableDate = dataObj.optJSONObject("date")?.optString("readable", dateStr) ?: dateStr

          Log.d(TAG, "Online prayer timings parsed successfully: $timingsMap")

          return@withContext OnlinePrayerResult(
            isSuccess = true,
            timings = timingsMap,
            dateFormatted = readableDate,
            dateKey = currentDayKey,
            sourceName = "Kementerian Agama RI (Metode 20)"
          )
        }
      } catch (e: Exception) {
        Log.w(TAG, "Failed requesting from $url: ${e.message}")
        lastError = e.localizedMessage ?: "Koneksi internet bermasalah"
      }
    }

    return@withContext OnlinePrayerResult(
      isSuccess = false,
      dateKey = currentDayKey,
      errorMessage = lastError ?: "Gagal terhubung ke server Kemenag"
    )
  }

  private fun calculateDhuhaFromSunrise(sunrise: String): String {
    try {
      val parts = sunrise.split(":")
      if (parts.size >= 2) {
        val h = parts[0].toInt()
        val m = parts[1].toInt()
        val totalMin = (h * 60 + m + 25) % 1440
        val dhH = totalMin / 60
        val dhM = totalMin % 60
        return String.format(Locale.US, "%02d:%02d", dhH, dhM)
      }
    } catch (_: Exception) {}
    return "05:45"
  }
}
