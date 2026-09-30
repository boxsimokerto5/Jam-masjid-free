package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.example.data.model.CityData
import com.example.data.model.CityPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

data class AutoLocationResult(
  val cityName: String,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val timezoneOffset: Double,
  val isGpsSuccess: Boolean
)

class LocationHelper(private val context: Context) {

  private val locationManager =
    context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

  @SuppressLint("MissingPermission")
  suspend fun getCurrentOrBestLocation(): AutoLocationResult = withContext(Dispatchers.IO) {
    var bestLocation: Location? = null

    try {
      if (locationManager != null) {
        val providers = locationManager.getProviders(true)
        for (provider in providers) {
          val l = locationManager.getLastKnownLocation(provider) ?: continue
          if (bestLocation == null || l.accuracy < bestLocation!!.accuracy) {
            bestLocation = l
          }
        }
      }
    } catch (e: Exception) {
      Log.w("LocationHelper", "Error getting last known location: ${e.message}")
    }

    if (bestLocation != null) {
      val lat = bestLocation.latitude
      val lng = bestLocation.longitude
      resolveLocationDetails(lat, lng)
    } else {
      // Fallback to Kediri / Default
      val defaultCity = CityData.cities.firstOrNull { it.name == "Kediri" } ?: CityData.cities[0]
      AutoLocationResult(
        cityName = defaultCity.name,
        address = "${defaultCity.name}, ${defaultCity.province}",
        latitude = defaultCity.latitude,
        longitude = defaultCity.longitude,
        timezoneOffset = defaultCity.timezoneOffsetHours,
        isGpsSuccess = false
      )
    }
  }

  suspend fun resolveLocationDetails(lat: Double, lng: Double): AutoLocationResult = withContext(Dispatchers.IO) {
    var detectedCity = ""
    var detectedAddress = ""

    // 1. Try Geocoder
    try {
      if (Geocoder.isPresent()) {
        val geocoder = Geocoder(context, Locale("id", "ID"))
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (!addresses.isNullOrEmpty()) {
          val addr = addresses[0]
          val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
          val province = addr.adminArea ?: ""
          if (!locality.isNullOrBlank()) {
            detectedCity = locality.replace("Kabupaten", "Kab.").replace("Kota", "").trim()
            detectedAddress = if (province.isNotBlank()) "$detectedCity, $province" else detectedCity
          }
        }
      }
    } catch (e: Exception) {
      Log.w("LocationHelper", "Geocoder reverse lookup failed: ${e.message}")
    }

    // 2. If Geocoder didn't find specific name, use nearest known Indonesian city
    val nearest = CityData.findNearestCity(lat, lng)
    if (detectedCity.isBlank()) {
      detectedCity = nearest.name
      detectedAddress = "${nearest.name}, ${nearest.province}"
    }

    // 3. Determine Timezone Offset
    // In Indonesia: WIB (UTC+7), WITA (UTC+8), WIT (UTC+9)
    val tzOffset = when {
      lng < 114.5 -> 7.0   // Jawa, Sumatera, Kalbar, Kalteng -> WIB
      lng < 125.5 -> 8.0   // Bali, NTB, NTT, Kalsel, Kaltim, Kaltara, Sulawesi -> WITA
      else -> 9.0          // Maluku, Papua -> WIT
    }

    AutoLocationResult(
      cityName = detectedCity,
      address = detectedAddress,
      latitude = lat,
      longitude = lng,
      timezoneOffset = tzOffset,
      isGpsSuccess = true
    )
  }
}
