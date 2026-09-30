package com.example.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CityPreset(
  val name: String,
  val province: String,
  val latitude: Double,
  val longitude: Double,
  val timezoneOffsetHours: Double
)

object CityData {
  val cities = listOf(
    // Jawa Timur
    CityPreset("Kediri", "Jawa Timur", -7.8480, 112.0178, 7.0),
    CityPreset("Surabaya", "Jawa Timur", -7.2575, 112.7521, 7.0),
    CityPreset("Malang", "Jawa Timur", -7.9797, 112.6304, 7.0),
    CityPreset("Sidoarjo", "Jawa Timur", -7.4726, 112.6675, 7.0),
    CityPreset("Gresik", "Jawa Timur", -7.1566, 112.6555, 7.0),
    CityPreset("Jember", "Jawa Timur", -8.1724, 113.6995, 7.0),
    CityPreset("Banyuwangi", "Jawa Timur", -8.2192, 114.3692, 7.0),
    CityPreset("Madiun", "Jawa Timur", -7.6298, 111.5239, 7.0),
    CityPreset("Blitar", "Jawa Timur", -8.0983, 112.1681, 7.0),
    CityPreset("Tulungagung", "Jawa Timur", -8.0652, 111.9015, 7.0),
    CityPreset("Nganjuk", "Jawa Timur", -7.6040, 111.9042, 7.0),
    CityPreset("Mojokerto", "Jawa Timur", -7.4705, 112.4401, 7.0),
    CityPreset("Pasuruan", "Jawa Timur", -7.6453, 112.9075, 7.0),
    CityPreset("Probolinggo", "Jawa Timur", -7.7543, 113.2159, 7.0),
    CityPreset("Bojonegoro", "Jawa Timur", -7.1502, 111.8817, 7.0),
    CityPreset("Tuban", "Jawa Timur", -6.8976, 112.0649, 7.0),
    CityPreset("Lamongan", "Jawa Timur", -7.1197, 112.4158, 7.0),

    // DKI Jakarta & Banten & Jawa Barat
    CityPreset("Jakarta Pusat", "DKI Jakarta", -6.2088, 106.8456, 7.0),
    CityPreset("Bandung", "Jawa Barat", -6.9175, 107.6191, 7.0),
    CityPreset("Bekasi", "Jawa Barat", -6.2383, 106.9756, 7.0),
    CityPreset("Depok", "Jawa Barat", -6.4025, 106.7942, 7.0),
    CityPreset("Bogor", "Jawa Barat", -6.5971, 106.8060, 7.0),
    CityPreset("Tangerang", "Banten", -6.1783, 106.6319, 7.0),
    CityPreset("Tangerang Selatan", "Banten", -6.2838, 106.7118, 7.0),
    CityPreset("Serang", "Banten", -6.1104, 106.1640, 7.0),
    CityPreset("Cirebon", "Jawa Barat", -6.7320, 108.5523, 7.0),
    CityPreset("Tasikmalaya", "Jawa Barat", -7.3274, 108.2207, 7.0),
    CityPreset("Sukabumi", "Jawa Barat", -6.9277, 106.9300, 7.0),

    // Jawa Tengah & D.I. Yogyakarta
    CityPreset("Semarang", "Jawa Tengah", -6.9667, 110.4167, 7.0),
    CityPreset("Solo / Surakarta", "Jawa Tengah", -7.5755, 110.8243, 7.0),
    CityPreset("Yogyakarta", "D.I. Yogyakarta", -7.7956, 110.3695, 7.0),
    CityPreset("Sleman", "D.I. Yogyakarta", -7.7156, 110.3556, 7.0),
    CityPreset("Bantul", "D.I. Yogyakarta", -7.8920, 110.3340, 7.0),
    CityPreset("Magelang", "Jawa Tengah", -7.4706, 110.2178, 7.0),
    CityPreset("Pekalongan", "Jawa Tengah", -6.8886, 109.6753, 7.0),
    CityPreset("Tegal", "Jawa Tengah", -6.8694, 109.1402, 7.0),
    CityPreset("Purwokerto / Banyumas", "Jawa Tengah", -7.4243, 109.2302, 7.0),
    CityPreset("Kudus", "Jawa Tengah", -6.8048, 110.8405, 7.0),
    CityPreset("Cilacap", "Jawa Tengah", -7.7032, 109.0159, 7.0),

    // Sumatera
    CityPreset("Banda Aceh", "Aceh", 5.5483, 95.3238, 7.0),
    CityPreset("Medan", "Sumatera Utara", 3.5952, 98.6722, 7.0),
    CityPreset("Padang", "Sumatera Barat", -0.9471, 100.4172, 7.0),
    CityPreset("Pekanbaru", "Riau", 0.5071, 101.4478, 7.0),
    CityPreset("Batam", "Kepulauan Riau", 1.1301, 104.0529, 7.0),
    CityPreset("Jambi", "Jambi", -1.6101, 103.6131, 7.0),
    CityPreset("Palembang", "Sumatera Selatan", -2.9761, 104.7754, 7.0),
    CityPreset("Bengkulu", "Bengkulu", -3.7928, 102.2608, 7.0),
    CityPreset("Bandar Lampung", "Lampung", -5.4500, 105.2667, 7.0),
    CityPreset("Pangkalpinang", "Bangka Belitung", -2.1333, 106.1167, 7.0),

    // Kalimantan
    CityPreset("Pontianak", "Kalimantan Barat", -0.0263, 109.3425, 7.0),
    CityPreset("Banjarmasin", "Kalimantan Selatan", -3.3167, 114.5901, 8.0),
    CityPreset("Samarinda", "Kalimantan Timur", -0.5022, 117.1536, 8.0),
    CityPreset("Balikpapan", "Kalimantan Timur", -1.2379, 116.8529, 8.0),
    CityPreset("Palangka Raya", "Kalimantan Tengah", -2.2161, 113.9139, 7.0),
    CityPreset("Tarakan", "Kalimantan Utara", 3.3273, 117.5786, 8.0),

    // Bali & Nusa Tenggara
    CityPreset("Denpasar", "Bali", -8.6705, 115.2126, 8.0),
    CityPreset("Mataram / Lombok", "Nusa Tenggara Barat", -8.5833, 116.1167, 8.0),
    CityPreset("Kupang", "Nusa Tenggara Timur", -10.1772, 123.6070, 8.0),

    // Sulawesi
    CityPreset("Makassar", "Sulawesi Selatan", -5.1477, 119.4327, 8.0),
    CityPreset("Manado", "Sulawesi Utara", 1.4748, 124.8421, 8.0),
    CityPreset("Palu", "Sulawesi Tengah", -0.9003, 119.8779, 8.0),
    CityPreset("Kendari", "Sulawesi Tenggara", -3.9985, 122.5126, 8.0),
    CityPreset("Gorontalo", "Gorontalo", 0.5435, 123.0568, 8.0),
    CityPreset("Mamuju", "Sulawesi Barat", -2.6775, 118.8894, 8.0),

    // Maluku & Papua
    CityPreset("Ambon", "Maluku", -3.6554, 128.1908, 9.0),
    CityPreset("Ternate", "Maluku Utara", 0.7904, 127.3807, 9.0),
    CityPreset("Jayapura", "Papua", -2.5337, 140.7181, 9.0),
    CityPreset("Sorong", "Papua Barat Daya", -0.8762, 131.2558, 9.0),
    CityPreset("Manokwari", "Papua Barat", -0.8615, 134.0620, 9.0),
    CityPreset("Merauke", "Papua Selatan", -8.4991, 140.4019, 9.0)
  )

  fun findNearestCity(lat: Double, lng: Double): CityPreset {
    var nearest = cities[0]
    var minDistance = Double.MAX_VALUE

    for (city in cities) {
      val dist = distanceBetween(lat, lng, city.latitude, city.longitude)
      if (dist < minDistance) {
        minDistance = dist
        nearest = city
      }
    }
    return nearest
  }

  private fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Radius bumi dalam km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
  }
}
