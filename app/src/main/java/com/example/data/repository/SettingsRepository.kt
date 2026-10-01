package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.MosqueSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("mosque_display_prefs", Context.MODE_PRIVATE)

  private val _settingsFlow = MutableStateFlow(loadSettings())
  val settingsFlow: StateFlow<MosqueSettings> = _settingsFlow.asStateFlow()

  private fun loadSettings(): MosqueSettings {
    val rawRunning = prefs.getString("running_texts_pipe", null)
    val runningTexts = if (!rawRunning.isNullOrEmpty()) {
      rawRunning.split("|||").filter { it.isNotBlank() }
    } else {
      listOf(
        "Selamat datang di Masjid Agung Al-Kautsar Kediri. Lurus dan rapatkan shaf saat sholat berjamaah.",
        "Rasulullah SAW bersabda: 'Siapa yang membangun masjid karena Allah, maka Allah bangunkan baginya rumah di surga.' (HR. Bukhari)",
        "Infaq & Sedekah renovasi masjid dapat disalurkan melalui BSI No. Rek 7123456789 a.n. Kas Masjid Al-Kautsar.",
        "Kajian Tafsir & Fiqih Rutin setiap malam Jum'at Ba'da Maghrib bersama Ustadz Pengasuh Masjid.",
        "Mohon menonaktifkan atau mengheningkan nada dering handphone sebelum memulai sholat."
      )
    }

    return MosqueSettings(
      mosqueName = prefs.getString("mosque_name", "MASJID AGUNG AL-KAUTSAR") ?: "MASJID AGUNG AL-KAUTSAR",
      mosqueAddress = prefs.getString("mosque_address", "Kota Kediri, Jawa Timur") ?: "Kota Kediri, Jawa Timur",
      cityName = prefs.getString("city_name", "Kediri") ?: "Kediri",
      latitude = java.lang.Double.longBitsToDouble(prefs.getLong("latitude", java.lang.Double.doubleToLongBits(-7.8480))),
      longitude = java.lang.Double.longBitsToDouble(prefs.getLong("longitude", java.lang.Double.doubleToLongBits(112.0178))),
      timezoneOffset = java.lang.Double.longBitsToDouble(prefs.getLong("tz_offset", java.lang.Double.doubleToLongBits(7.0))),
      hijriAdjustmentDays = prefs.getInt("hijri_adj", 0),
      useOnlineSchedule = prefs.getBoolean("use_online_sched", true),
      onlineSource = prefs.getString("online_source", "Kemenag RI (Aladhan API)") ?: "Kemenag RI (Aladhan API)",
      lastOnlineSyncFormatted = prefs.getString("last_online_sync_fmt", "") ?: "",
      cachedOnlineTimesJson = prefs.getString("cached_online_times_json", "") ?: "",
      cachedOnlineDate = prefs.getString("cached_online_date", "") ?: "",
      backgroundType = prefs.getString("bg_type", "preset_twilight") ?: "preset_twilight",
      customBackgroundUri = prefs.getString("custom_bg_uri", "") ?: "",
      overlayDarkness = prefs.getFloat("overlay_darkness", 0.50f),
      runningTexts = runningTexts,
      runningTextSpeed = prefs.getInt("running_speed", 45),
      iqomahSubuh = prefs.getInt("iqomah_subuh", 15),
      iqomahDzuhur = prefs.getInt("iqomah_dzuhur", 10),
      iqomahAshar = prefs.getInt("iqomah_ashar", 10),
      iqomahMaghrib = prefs.getInt("iqomah_maghrib", 8),
      iqomahIsya = prefs.getInt("iqomah_isya", 10),
      sholatBlankMinutes = prefs.getInt("sholat_blank_min", 12),
      correctionSubuh = prefs.getInt("corr_subuh", 2),
      correctionDzuhur = prefs.getInt("corr_dzuhur", 2),
      correctionAshar = prefs.getInt("corr_ashar", 2),
      correctionMaghrib = prefs.getInt("corr_maghrib", 2),
      correctionIsya = prefs.getInt("corr_isya", 2),
      correctionImsak = prefs.getInt("corr_imsak", 2),
      kasSaldo = prefs.getLong("kas_saldo", 28750000L),
      kasPemasukan = prefs.getLong("kas_in", 5200000L),
      kasPengeluaran = prefs.getLong("kas_out", 1850000L),
      jumatKhotib = prefs.getString("jumat_khotib", "Dr. H. Ahmad Fauzi, M.Ag") ?: "Dr. H. Ahmad Fauzi, M.Ag",
      jumatImam = prefs.getString("jumat_imam", "Ust. Muhammad Ridwan, Al-Hafidz") ?: "Ust. Muhammad Ridwan, Al-Hafidz",
      jumatMuadzin = prefs.getString("jumat_muadzin", "Ust. Bilal Ramadhan") ?: "Ust. Bilal Ramadhan",
      isSoundAlertEnabled = prefs.getBoolean("sound_alert", true)
    )
  }

  fun updateSettings(newSettings: MosqueSettings) {
    prefs.edit().apply {
      putString("mosque_name", newSettings.mosqueName)
      putString("mosque_address", newSettings.mosqueAddress)
      putString("city_name", newSettings.cityName)
      putLong("latitude", java.lang.Double.doubleToLongBits(newSettings.latitude))
      putLong("longitude", java.lang.Double.doubleToLongBits(newSettings.longitude))
      putLong("tz_offset", java.lang.Double.doubleToLongBits(newSettings.timezoneOffset))
      putInt("hijri_adj", newSettings.hijriAdjustmentDays)
      putBoolean("use_online_sched", newSettings.useOnlineSchedule)
      putString("online_source", newSettings.onlineSource)
      putString("last_online_sync_fmt", newSettings.lastOnlineSyncFormatted)
      putString("cached_online_times_json", newSettings.cachedOnlineTimesJson)
      putString("cached_online_date", newSettings.cachedOnlineDate)
      putString("bg_type", newSettings.backgroundType)
      putString("custom_bg_uri", newSettings.customBackgroundUri)
      putFloat("overlay_darkness", newSettings.overlayDarkness)
      putString("running_texts_pipe", newSettings.runningTexts.joinToString("|||"))
      putInt("running_speed", newSettings.runningTextSpeed)
      putInt("iqomah_subuh", newSettings.iqomahSubuh)
      putInt("iqomah_dzuhur", newSettings.iqomahDzuhur)
      putInt("iqomah_ashar", newSettings.iqomahAshar)
      putInt("iqomah_maghrib", newSettings.iqomahMaghrib)
      putInt("iqomah_isya", newSettings.iqomahIsya)
      putInt("sholat_blank_min", newSettings.sholatBlankMinutes)
      putInt("corr_subuh", newSettings.correctionSubuh)
      putInt("corr_dzuhur", newSettings.correctionDzuhur)
      putInt("corr_ashar", newSettings.correctionAshar)
      putInt("corr_maghrib", newSettings.correctionMaghrib)
      putInt("corr_isya", newSettings.correctionIsya)
      putInt("corr_imsak", newSettings.correctionImsak)
      putLong("kas_saldo", newSettings.kasSaldo)
      putLong("kas_in", newSettings.kasPemasukan)
      putLong("kas_out", newSettings.kasPengeluaran)
      putString("jumat_khotib", newSettings.jumatKhotib)
      putString("jumat_imam", newSettings.jumatImam)
      putString("jumat_muadzin", newSettings.jumatMuadzin)
      putBoolean("sound_alert", newSettings.isSoundAlertEnabled)
      apply()
    }
    _settingsFlow.value = newSettings
  }
}
