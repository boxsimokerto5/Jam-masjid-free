package com.example.data.model

data class MosqueSettings(
  val mosqueName: String = "MASJID AGUNG AL-KAUTSAR",
  val mosqueAddress: String = "Kota Kediri, Jawa Timur",
  val cityName: String = "Kediri",
  val latitude: Double = -7.8480,
  val longitude: Double = 112.0178,
  val timezoneOffset: Double = 7.0,
  val hijriAdjustmentDays: Int = 0,
  val backgroundType: String = "preset_twilight", // preset_twilight, preset_emerald, gradient_emerald, gradient_midnight, gradient_sunset, custom_uri
  val customBackgroundUri: String = "",
  val overlayDarkness: Float = 0.50f, // 0.2f to 0.85f
  val runningTexts: List<String> = listOf(
    "Selamat datang di Masjid Agung Al-Kautsar Kediri. Lurus dan rapatkan shaf saat sholat berjamaah.",
    "Rasulullah SAW bersabda: 'Siapa yang membangun masjid karena Allah, maka Allah bangunkan baginya rumah di surga.' (HR. Bukhari)",
    "Infaq & Sedekah renovasi masjid dapat disalurkan melalui BSI No. Rek 7123456789 a.n. Kas Masjid Al-Kautsar.",
    "Kajian Tafsir & Fiqih Rutin setiap malam Jum'at Ba'da Maghrib bersama Ustadz Pengasuh Masjid.",
    "Mohon menonaktifkan atau mengheningkan nada dering handphone sebelum memulai sholat."
  ),
  val runningTextSpeed: Int = 45, // seconds to traverse or speed unit
  val iqomahSubuh: Int = 15,
  val iqomahDzuhur: Int = 10,
  val iqomahAshar: Int = 10,
  val iqomahMaghrib: Int = 8,
  val iqomahIsya: Int = 10,
  val sholatBlankMinutes: Int = 12,
  val correctionSubuh: Int = 2,
  val correctionDzuhur: Int = 2,
  val correctionAshar: Int = 2,
  val correctionMaghrib: Int = 2,
  val correctionIsya: Int = 2,
  val correctionImsak: Int = 2,
  val kasSaldo: Long = 28750000L,
  val kasPemasukan: Long = 5200000L,
  val kasPengeluaran: Long = 1850000L,
  val jumatKhotib: String = "Dr. H. Ahmad Fauzi, M.Ag",
  val jumatImam: String = "Ust. Muhammad Ridwan, Al-Hafidz",
  val jumatMuadzin: String = "Ust. Bilal Ramadhan",
  val isSoundAlertEnabled: Boolean = true
) {
  fun getIqomahMinutesFor(type: PrayerType): Int {
    return when (type) {
      PrayerType.SUBUH -> iqomahSubuh
      PrayerType.DZUHUR -> iqomahDzuhur
      PrayerType.ASHAR -> iqomahAshar
      PrayerType.MAGHRIB -> iqomahMaghrib
      PrayerType.ISYA -> iqomahIsya
      else -> 10
    }
  }

  fun getCorrectionFor(type: PrayerType): Int {
    return when (type) {
      PrayerType.IMSAK -> correctionImsak
      PrayerType.SUBUH -> correctionSubuh
      PrayerType.DZUHUR -> correctionDzuhur
      PrayerType.ASHAR -> correctionAshar
      PrayerType.MAGHRIB -> correctionMaghrib
      PrayerType.ISYA -> correctionIsya
      else -> 2
    }
  }
}
