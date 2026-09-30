package com.example.data.prayer

import com.example.data.model.MosqueSettings
import com.example.data.model.PrayerTimeItem
import com.example.data.model.PrayerType
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

object PrayerCalculator {

  private const val DEG_TO_RAD = Math.PI / 180.0
  private const val RAD_TO_DEG = 180.0 / Math.PI

  private fun fixAngle(a: Double): Double {
    var angle = a - 360.0 * floor(a / 360.0)
    if (angle < 0) angle += 360.0
    return angle
  }

  private fun fixHour(h: Double): Double {
    var hour = h - 24.0 * floor(h / 24.0)
    if (hour < 0) hour += 24.0
    return hour
  }

  private fun sinDeg(deg: Double): Double = sin(deg * DEG_TO_RAD)
  private fun cosDeg(deg: Double): Double = cos(deg * DEG_TO_RAD)
  private fun tanDeg(deg: Double): Double = tan(deg * DEG_TO_RAD)
  private fun asinDeg(x: Double): Double = asin(x.coerceIn(-1.0, 1.0)) * RAD_TO_DEG
  private fun acosDeg(x: Double): Double = acos(x.coerceIn(-1.0, 1.0)) * RAD_TO_DEG
  private fun atanDeg(x: Double): Double = atan(x) * RAD_TO_DEG

  /**
   * Calculates sun position: Julian Day, Declination, Equation of Time
   */
  private fun sunPosition(jd: Double): Pair<Double, Double> {
    val d = jd - 2451545.0
    val g = fixAngle(357.529 + 0.98560028 * d)
    val q = fixAngle(280.459 + 0.98564736 * d)
    val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))
    val e = 23.439 - 0.00000036 * d
    val dAngle = asinDeg(sinDeg(e) * sinDeg(l))
    var ra = atanDeg(cosDeg(e) * sinDeg(l)) / 15.0
    val lHour = l / 15.0
    ra = fixHour(ra + (floor(lHour / 6.0) - floor(ra / 6.0)) * 6.0)
    val eqt = q / 15.0 - ra
    return Pair(dAngle, eqt)
  }

  private fun julianDay(year: Int, month: Int, day: Int): Double {
    var y = year
    var m = month
    if (m <= 2) {
      y -= 1
      m += 12
    }
    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4.0)
    return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
  }

  fun calculatePrayerTimes(
    calendar: Calendar,
    settings: MosqueSettings
  ): List<PrayerTimeItem> {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val jd = julianDay(year, month, day)
    val (declination, eqt) = sunPosition(jd)

    val lat = settings.latitude
    val lng = settings.longitude
    val tz = settings.timezoneOffset

    // Solar noon (Dzuhur base)
    val noon = fixHour(12.0 + tz - lng / 15.0 - eqt)

    // Sun angles for Indonesian Kemenag convention
    // Fajr (Subuh): 20 deg
    // Isha (Isya): 18 deg
    // Sunrise/Sunset: 0.833 deg
    fun hourAngle(angle: Double): Double {
      val cosHA = (-sinDeg(angle) - sinDeg(lat) * sinDeg(declination)) / (cosDeg(lat) * cosDeg(declination))
      return if (cosHA in -1.0..1.0) acosDeg(cosHA) / 15.0 else 0.0
    }

    val fajrHA = hourAngle(20.0)
    val sunriseHA = hourAngle(0.833)
    val ishaHA = hourAngle(18.0)

    // Asr (Shafi'i shadow = 1)
    val asrAngle = -atanDeg(1.0 / (1.0 + tanDeg(abs(lat - declination))))
    val asrHA = hourAngle(-asrAngle)

    // Base decimal hours
    val subuhBase = fixHour(noon - fajrHA)
    val syuruqBase = fixHour(noon - sunriseHA)
    val dzuhurBase = fixHour(noon + (2.0 / 60.0)) // 2 min ihtiyat default
    val asharBase = fixHour(noon + asrHA)
    val maghribBase = fixHour(noon + sunriseHA)
    val isyaBase = fixHour(noon + ishaHA)

    fun toMinutes(baseHour: Double, correctionMinutes: Int): Int {
      val totalMinutes = (baseHour * 60.0).toInt() + correctionMinutes
      return (totalMinutes + 1440) % 1440
    }

    val subuhMin = toMinutes(subuhBase, settings.correctionSubuh)
    val imsakMin = (subuhMin - 10 + 1440) % 1440
    val syuruqMin = toMinutes(syuruqBase, 0)
    val dhuhaMin = (syuruqMin + 22 + 1440) % 1440
    val dzuhurMin = toMinutes(dzuhurBase, settings.correctionDzuhur)
    val asharMin = toMinutes(asharBase, settings.correctionAshar)
    val maghribMin = toMinutes(maghribBase, settings.correctionMaghrib)
    val isyaMin = toMinutes(isyaBase, settings.correctionIsya)

    fun formatMin(min: Int): String {
      val h = min / 60
      val m = min % 60
      return String.format(Locale.US, "%02d:%02d", h, m)
    }

    val items = listOf(
      PrayerTimeItem(PrayerType.IMSAK, formatMin(imsakMin), imsakMin / 60, imsakMin % 60),
      PrayerTimeItem(PrayerType.SUBUH, formatMin(subuhMin), subuhMin / 60, subuhMin % 60),
      PrayerTimeItem(PrayerType.TERBIT, formatMin(syuruqMin), syuruqMin / 60, syuruqMin % 60),
      PrayerTimeItem(PrayerType.DHUHA, formatMin(dhuhaMin), dhuhaMin / 60, dhuhaMin % 60),
      PrayerTimeItem(PrayerType.DZUHUR, formatMin(dzuhurMin), dzuhurMin / 60, dzuhurMin % 60),
      PrayerTimeItem(PrayerType.ASHAR, formatMin(asharMin), asharMin / 60, asharMin % 60),
      PrayerTimeItem(PrayerType.MAGHRIB, formatMin(maghribMin), maghribMin / 60, maghribMin % 60),
      PrayerTimeItem(PrayerType.ISYA, formatMin(isyaMin), isyaMin / 60, isyaMin % 60)
    )

    // Determine current/upcoming
    val currentMinuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    var upcomingFound = false

    return items.map { item ->
      val itemMin = item.hour * 60 + item.minute
      val isPassed = itemMin <= currentMinuteOfDay
      val isUpcoming = if (!upcomingFound && itemMin > currentMinuteOfDay) {
        upcomingFound = true
        true
      } else false

      item.copy(isUpcoming = isUpcoming, isPassed = isPassed)
    }
  }
}
