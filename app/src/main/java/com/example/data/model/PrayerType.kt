package com.example.data.model

enum class PrayerType(
  val displayName: String,
  val arabicName: String,
  val isFardhu: Boolean
) {
  IMSAK("Imsak", "الإمساك", false),
  SUBUH("Subuh", "الفجر", true),
  TERBIT("Syuruq", "الشروق", false),
  DHUHA("Dhuha", "الضحى", false),
  DZUHUR("Dzuhur", "الظهر", true),
  ASHAR("Ashar", "العصر", true),
  MAGHRIB("Maghrib", "المغرب", true),
  ISYA("Isya'", "العشاء", true)
}

data class PrayerTimeItem(
  val type: PrayerType,
  val timeString: String,      // HH:mm
  val hour: Int,
  val minute: Int,
  val isUpcoming: Boolean = false,
  val isPassed: Boolean = false
)

enum class MosqueDisplayState {
  NORMAL,
  ADZAN,
  IQOMAH,
  SHOLAT_MODE
}
