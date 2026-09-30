package com.example.data.calendar

import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object HijriCalendarHelper {

  private val HIJRI_MONTHS = arrayOf(
    "Muharram",
    "Safar",
    "Rabi'ul Awwal",
    "Rabi'ul Akhir",
    "Jumadil Awwal",
    "Jumadil Akhir",
    "Rajab",
    "Sya'ban",
    "Ramadhan",
    "Syawwal",
    "Dzulqa'dah",
    "Dzulhijjah"
  )

  private val GREGORIAN_MONTHS = arrayOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
  )

  private val HARI_INDONESIA = arrayOf(
    "", "Ahad", "Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu"
  )

  fun getGregorianFormatted(cal: Calendar): String {
    val dayOfWeek = HARI_INDONESIA[cal.get(Calendar.DAY_OF_WEEK)]
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = GREGORIAN_MONTHS[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    return "$dayOfWeek, $day $month $year"
  }

  fun getHijriFormatted(cal: Calendar, adjustmentDays: Int = 0): String {
    val adjustedCal = (cal.clone() as Calendar).apply {
      add(Calendar.DAY_OF_YEAR, adjustmentDays)
    }

    val day = adjustedCal.get(Calendar.DAY_OF_MONTH)
    val month = adjustedCal.get(Calendar.MONTH) + 1
    val year = adjustedCal.get(Calendar.YEAR)

    // Kuwaiti Algorithm for Hijri conversion
    var m = month
    var y = year
    if (m < 3) {
      y -= 1
      m += 12
    }

    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4.0)
    val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524

    val z = jd - 1948440 + 10632
    val n = floor((z - 1) / 10631.0)
    val zMod = z - 10631 * n + 354
    val j = (floor((10985 - zMod) / 5316.0)) * (floor((50 * zMod) / 17719.0)) +
        (floor(zMod / 5670.0)) * (floor((43 * zMod) / 15238.0))
    val zModSub = zMod - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) -
        (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
    val mHijri = floor((24 * zModSub) / 709.0).toInt()
    val dHijri = (zModSub - floor((709 * mHijri) / 24.0)).toInt()
    val yHijri = (30 * n + j - 30).toInt()

    val safeMonthIdx = (mHijri - 1).coerceIn(0, 11)
    val monthName = HIJRI_MONTHS[safeMonthIdx]

    return String.format(Locale.US, "%d %s %d H", dHijri, monthName, yHijri)
  }
}
