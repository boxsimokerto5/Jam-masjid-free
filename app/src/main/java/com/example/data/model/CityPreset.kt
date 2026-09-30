package com.example.data.model

data class CityPreset(
  val name: String,
  val province: String,
  val latitude: Double,
  val longitude: Double,
  val timezoneOffsetHours: Double
)

object CityData {
  val cities = listOf(
    CityPreset("Kediri", "Jawa Timur", -7.8480, 112.0178, 7.0),
    CityPreset("Surabaya", "Jawa Timur", -7.2575, 112.7521, 7.0),
    CityPreset("Malang", "Jawa Timur", -7.9797, 112.6304, 7.0),
    CityPreset("Jakarta", "DKI Jakarta", -6.2088, 106.8456, 7.0),
    CityPreset("Bandung", "Jawa Barat", -6.9175, 107.6191, 7.0),
    CityPreset("Semarang", "Jawa Tengah", -6.9667, 110.4167, 7.0),
    CityPreset("Yogyakarta", "D.I. Yogyakarta", -7.7956, 110.3695, 7.0),
    CityPreset("Solo / Surakarta", "Jawa Tengah", -7.5755, 110.8243, 7.0),
    CityPreset("Medan", "Sumatera Utara", 3.5952, 98.6722, 7.0),
    CityPreset("Palembang", "Sumatera Selatan", -2.9761, 104.7754, 7.0),
    CityPreset("Padang", "Sumatera Barat", -0.9471, 100.4172, 7.0),
    CityPreset("Makassar", "Sulawesi Selatan", -5.1477, 119.4327, 8.0),
    CityPreset("Denpasar", "Bali", -8.6705, 115.2126, 8.0),
    CityPreset("Banjarmasin", "Kalimantan Selatan", -3.3167, 114.5901, 8.0),
    CityPreset("Samarinda", "Kalimantan Timur", -0.5022, 117.1536, 8.0),
    CityPreset("Jayapura", "Papua", -2.5337, 140.7181, 9.0)
  )
}
