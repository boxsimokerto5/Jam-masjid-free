package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calendar.HijriCalendarHelper
import com.example.data.model.CityData
import com.example.data.model.CityPreset
import com.example.data.model.MosqueDisplayState
import com.example.data.model.MosqueSettings
import com.example.data.model.PrayerTimeItem
import com.example.data.model.PrayerType
import com.example.data.prayer.PrayerCalculator
import com.example.data.repository.SettingsRepository
import com.example.data.sound.MosqueSoundHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class MosqueUiState(
  val settings: MosqueSettings = MosqueSettings(),
  val timeFormatted: String = "00:00",
  val secondsFormatted: String = "00",
  val gregorianDate: String = "",
  val hijriDate: String = "",
  val prayerTimes: List<PrayerTimeItem> = emptyList(),
  val nextPrayer: PrayerTimeItem? = null,
  val timeUntilNextPrayer: String = "00:00:00",
  val displayState: MosqueDisplayState = MosqueDisplayState.NORMAL,
  val specialStatePrayerName: String = "",
  val iqomahSecondsLeft: Int = 0,
  val sholatMinutesLeft: Int = 0,
  val activeInfoSlideIndex: Int = 0, // 0: Kas, 1: Petugas Jumat, 2: Hadits
  val isForcedTvMode: Boolean = false,
  val showCastGuideDialog: Boolean = false
)

class MosqueClockViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = SettingsRepository(application.applicationContext)
  private val soundHelper = MosqueSoundHelper(application.applicationContext)

  private val _uiState = MutableStateFlow(MosqueUiState(settings = repository.settingsFlow.value))
  val uiState: StateFlow<MosqueUiState> = _uiState.asStateFlow()

  private var lastTriggeredPrayerMinute: String = ""

  init {
    // Observe settings changes
    viewModelScope.launch {
      repository.settingsFlow.collect { settings ->
        _uiState.update { it.copy(settings = settings) }
        recalculateTimes()
      }
    }

    // Main clock ticker loop (ticks every 1 second)
    viewModelScope.launch {
      var slideCounter = 0
      while (isActive) {
        val now = Calendar.getInstance()
        val h = now.get(Calendar.HOUR_OF_DAY)
        val m = now.get(Calendar.MINUTE)
        val s = now.get(Calendar.SECOND)

        val timeStr = String.format(Locale.US, "%02d:%02d", h, m)
        val secStr = String.format(Locale.US, "%02d", s)
        val gregDate = HijriCalendarHelper.getGregorianFormatted(now)
        val hijriDate = HijriCalendarHelper.getHijriFormatted(now, _uiState.value.settings.hijriAdjustmentDays)

        // Slide carousel timer (every 10 seconds)
        slideCounter++
        val slideIdx = (slideCounter / 10) % 3

        _uiState.update { state ->
          state.copy(
            timeFormatted = timeStr,
            secondsFormatted = secStr,
            gregorianDate = gregDate,
            hijriDate = hijriDate,
            activeInfoSlideIndex = slideIdx
          )
        }

        handleStateTransitions(now, timeStr, s)
        updatePrayerCountdowns(now)

        delay(1000)
      }
    }
  }

  private fun recalculateTimes() {
    val now = Calendar.getInstance()
    val prayers = PrayerCalculator.calculatePrayerTimes(now, _uiState.value.settings)
    val next = prayers.firstOrNull { it.isUpcoming } ?: prayers.firstOrNull()

    _uiState.update { it.copy(prayerTimes = prayers, nextPrayer = next) }
  }

  private fun handleStateTransitions(now: Calendar, timeStr: String, currentSec: Int) {
    val currentDisplayState = _uiState.value.displayState

    // 1. Check for automatic Adzan entry
    if (currentDisplayState == MosqueDisplayState.NORMAL) {
      if (currentSec == 0 && timeStr != lastTriggeredPrayerMinute) {
        val matchingPrayer = _uiState.value.prayerTimes.firstOrNull { it.timeString == timeStr && it.type.isFardhu }
        if (matchingPrayer != null) {
          lastTriggeredPrayerMinute = timeStr
          triggerAdzan(matchingPrayer.type)
          return
        }
      }
    }

    // 2. Countdown progress
    when (currentDisplayState) {
      MosqueDisplayState.ADZAN -> {
        // Adzan banner stays for 3 minutes (or until manual/auto advance to Iqomah)
        val left = _uiState.value.iqomahSecondsLeft - 1
        if (left <= 0) {
          // Transition to Iqomah countdown
          val prayerType = PrayerType.values().firstOrNull { it.displayName == _uiState.value.specialStatePrayerName } ?: PrayerType.DZUHUR
          startIqomahCountdown(prayerType)
        } else {
          _uiState.update { it.copy(iqomahSecondsLeft = left) }
        }
      }

      MosqueDisplayState.IQOMAH -> {
        val left = _uiState.value.iqomahSecondsLeft - 1
        if (left <= 0) {
          // Iqomah finished! Play final bell and transition to Sholat Mode
          if (_uiState.value.settings.isSoundAlertEnabled) {
            soundHelper.playFinalIqomahBell()
          }
          startSholatMode()
        } else {
          if (left <= 10 && _uiState.value.settings.isSoundAlertEnabled) {
            soundHelper.playIqomahCountdownBeep()
          }
          _uiState.update { it.copy(iqomahSecondsLeft = left) }
        }
      }

      MosqueDisplayState.SHOLAT_MODE -> {
        val secondsTotal = _uiState.value.iqomahSecondsLeft - 1
        if (secondsTotal <= 0) {
          _uiState.update {
            it.copy(
              displayState = MosqueDisplayState.NORMAL,
              specialStatePrayerName = "",
              iqomahSecondsLeft = 0,
              sholatMinutesLeft = 0
            )
          }
        } else {
          _uiState.update {
            it.copy(
              iqomahSecondsLeft = secondsTotal,
              sholatMinutesLeft = (secondsTotal / 60) + 1
            )
          }
        }
      }

      MosqueDisplayState.NORMAL -> {
        // Nothing special to decrement
      }
    }
  }

  private fun updatePrayerCountdowns(now: Calendar) {
    val prayers = PrayerCalculator.calculatePrayerTimes(now, _uiState.value.settings)
    val next = prayers.firstOrNull { it.isUpcoming } ?: prayers.firstOrNull()

    var timeRemainingStr = "00:00:00"
    if (next != null) {
      val nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
      val nowSec = now.get(Calendar.SECOND)
      val targetMin = next.hour * 60 + next.minute

      var diffSeconds = (targetMin - nowMin) * 60 - nowSec
      if (diffSeconds < 0) {
        diffSeconds += 24 * 3600
      }

      val hours = diffSeconds / 3600
      val mins = (diffSeconds % 3600) / 60
      val secs = diffSeconds % 60
      timeRemainingStr = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)
    }

    _uiState.update {
      it.copy(
        prayerTimes = prayers,
        nextPrayer = next,
        timeUntilNextPrayer = timeRemainingStr
      )
    }
  }

  fun triggerAdzan(prayerType: PrayerType) {
    if (_uiState.value.settings.isSoundAlertEnabled) {
      soundHelper.playAdzanChime()
    }
    // Adzan display runs for 60 seconds before auto-iqomah begins (or test mode)
    _uiState.update {
      it.copy(
        displayState = MosqueDisplayState.ADZAN,
        specialStatePrayerName = prayerType.displayName,
        iqomahSecondsLeft = 60
      )
    }
  }

  fun startIqomahCountdown(prayerType: PrayerType, customSeconds: Int? = null) {
    val durationSeconds = customSeconds ?: (_uiState.value.settings.getIqomahMinutesFor(prayerType) * 60)
    _uiState.update {
      it.copy(
        displayState = MosqueDisplayState.IQOMAH,
        specialStatePrayerName = prayerType.displayName,
        iqomahSecondsLeft = durationSeconds
      )
    }
  }

  fun startSholatMode(customMinutes: Int? = null) {
    val durationSec = (customMinutes ?: _uiState.value.settings.sholatBlankMinutes) * 60
    _uiState.update {
      it.copy(
        displayState = MosqueDisplayState.SHOLAT_MODE,
        iqomahSecondsLeft = durationSec,
        sholatMinutesLeft = durationSec / 60
      )
    }
  }

  fun dismissSpecialState() {
    _uiState.update {
      it.copy(
        displayState = MosqueDisplayState.NORMAL,
        specialStatePrayerName = "",
        iqomahSecondsLeft = 0,
        sholatMinutesLeft = 0
      )
    }
  }

  fun toggleForcedTvMode() {
    _uiState.update { it.copy(isForcedTvMode = !it.isForcedTvMode) }
  }

  fun setCastGuideDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showCastGuideDialog = visible) }
  }

  fun updateSettings(newSettings: MosqueSettings) {
    repository.updateSettings(newSettings)
  }

  fun selectCity(city: CityPreset) {
    val current = _uiState.value.settings
    val updated = current.copy(
      cityName = city.name,
      mosqueAddress = "${city.name}, ${city.province}",
      latitude = city.latitude,
      longitude = city.longitude,
      timezoneOffset = city.timezoneOffsetHours
    )
    updateSettings(updated)
  }

  fun setBackgroundType(bgType: String) {
    val updated = _uiState.value.settings.copy(backgroundType = bgType)
    updateSettings(updated)
  }

  fun setCustomBackgroundUri(uriString: String) {
    val updated = _uiState.value.settings.copy(
      backgroundType = "custom_uri",
      customBackgroundUri = uriString
    )
    updateSettings(updated)
  }

  fun setOverlayDarkness(darkness: Float) {
    val updated = _uiState.value.settings.copy(overlayDarkness = darkness)
    updateSettings(updated)
  }

  fun addRunningText(text: String) {
    if (text.isBlank()) return
    val currentList = _uiState.value.settings.runningTexts.toMutableList()
    currentList.add(text.trim())
    updateSettings(_uiState.value.settings.copy(runningTexts = currentList))
  }

  fun removeRunningText(index: Int) {
    val currentList = _uiState.value.settings.runningTexts.toMutableList()
    if (index in currentList.indices) {
      currentList.removeAt(index)
      updateSettings(_uiState.value.settings.copy(runningTexts = currentList))
    }
  }

  fun updateKas(saldo: Long, masuk: Long, keluar: Long) {
    val updated = _uiState.value.settings.copy(
      kasSaldo = saldo,
      kasPemasukan = masuk,
      kasPengeluaran = keluar
    )
    updateSettings(updated)
  }

  fun updatePetugasJumat(khotib: String, imam: String, muadzin: String) {
    val updated = _uiState.value.settings.copy(
      jumatKhotib = khotib,
      jumatImam = imam,
      jumatMuadzin = muadzin
    )
    updateSettings(updated)
  }

  override fun onCleared() {
    super.onCleared()
    soundHelper.release()
  }
}
