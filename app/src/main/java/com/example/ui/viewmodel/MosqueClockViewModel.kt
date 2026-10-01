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
import com.example.data.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import com.example.data.billing.MosqueBillingManager
import com.example.data.model.MosqueSubscriber
import com.example.data.model.MosqueSupportTicket
import com.example.data.prayer.OnlinePrayerService
import com.example.data.repository.MosqueSubscriberRepository
import com.example.server.MosqueLocalPwaServer
import org.json.JSONObject
import java.text.SimpleDateFormat

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
  val isForcedTvMode: Boolean = true, // Default to true: directly opens default mosque clock display!
  val showCastGuideDialog: Boolean = false,
  val showSubscriptionDialog: Boolean = false,
  val isProSubscribed: Boolean = false,
  val isDetectingLocation: Boolean = false,
  val locationDetectionMessage: String = "",
  val isPwaServerRunning: Boolean = false,
  val pwaServerUrl: String = "",
  val isSyncingOnline: Boolean = false,
  val onlineSyncMessage: String = "",
  val isOnlineDataActive: Boolean = false,
  val showAdminPanelDialog: Boolean = false,
  val showMosqueAccountDialog: Boolean = false,
  val showPrivacyPolicyDialog: Boolean = false,
  val showReportIssueDialog: Boolean = false,
  val showPostPaymentRegistrationDialog: Boolean = false,
  val currentOrderId: String = "",
  val subscribersList: List<MosqueSubscriber> = emptyList(),
  val ticketsList: List<MosqueSupportTicket> = emptyList(),
  val isAdminLoading: Boolean = false,
  val currentDeviceId: String = "",
  val currentDeviceModel: String = ""
)

class MosqueClockViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = SettingsRepository(application.applicationContext)
  private val soundHelper = MosqueSoundHelper(application.applicationContext)
  private val locationHelper = LocationHelper(application.applicationContext)
  private val pwaServer = MosqueLocalPwaServer(application.applicationContext)
  val billingManager = MosqueBillingManager(application.applicationContext)
  val subscriberRepository = MosqueSubscriberRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(
    MosqueUiState(
      settings = repository.settingsFlow.value,
      currentDeviceId = subscriberRepository.deviceId,
      currentDeviceModel = subscriberRepository.deviceModel
    )
  )
  val uiState: StateFlow<MosqueUiState> = _uiState.asStateFlow()

  private var lastTriggeredPrayerMinute: String = ""

  init {
    // Observe subscription state
    viewModelScope.launch {
      billingManager.isProSubscribed.collect { subscribed ->
        _uiState.update { it.copy(isProSubscribed = subscribed) }
        if (subscribed) {
          recordSubscriptionSuccess()
        }
      }
    }

    // Observe subscriber database
    viewModelScope.launch {
      subscriberRepository.subscribersList.collect { list ->
        _uiState.update { it.copy(subscribersList = list) }
      }
    }

    // Observe support tickets
    viewModelScope.launch {
      subscriberRepository.ticketsList.collect { list ->
        _uiState.update { it.copy(ticketsList = list) }
      }
    }

    // Load initial subscriber database & tickets
    viewModelScope.launch {
      subscriberRepository.fetchAllSubscribers()
      subscriberRepository.fetchSupportTickets()
    }

    // Observe settings changes
    viewModelScope.launch {
      repository.settingsFlow.collect { settings ->
        _uiState.update { it.copy(settings = settings) }
        pwaServer.updateSettings(settings)
        recalculateTimes()
      }
    }

    // Observe PWA server states
    viewModelScope.launch {
      pwaServer.isRunning.collect { running ->
        _uiState.update { it.copy(isPwaServerRunning = running) }
      }
    }
    viewModelScope.launch {
      pwaServer.serverUrl.collect { url ->
        _uiState.update { it.copy(pwaServerUrl = url) }
      }
    }

    // Auto-start PWA server so Smart TV can connect immediately
    pwaServer.updateSettings(repository.settingsFlow.value)
    pwaServer.startServer()

    // Otomatis sinkronisasi jadwal sholat dari server online Kemenag RI
    viewModelScope.launch {
      delay(600)
      val current = _uiState.value.settings
      if (current.useOnlineSchedule) {
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
        val needsSync = current.cachedOnlineDate != todayKey || current.cachedOnlineTimesJson.isBlank()
        syncOnlinePrayerTimes(force = needsSync)
      }
    }

    // Main clock ticker loop (ticks every 1 second)
    viewModelScope.launch {
      var slideCounter = 0
      var lastCheckedDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

      while (isActive) {
        val now = Calendar.getInstance()
        val h = now.get(Calendar.HOUR_OF_DAY)
        val m = now.get(Calendar.MINUTE)
        val s = now.get(Calendar.SECOND)

        // Otomatis sinkronisasi saat pergantian hari / tengah malam (00:00:00)
        val currentDayOfYear = now.get(Calendar.DAY_OF_YEAR)
        if (currentDayOfYear != lastCheckedDayOfYear) {
          lastCheckedDayOfYear = currentDayOfYear
          if (_uiState.value.settings.useOnlineSchedule) {
            syncOnlinePrayerTimes(force = true)
          }
        }

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
    val isOnline = _uiState.value.settings.useOnlineSchedule && _uiState.value.settings.cachedOnlineTimesJson.isNotBlank()

    _uiState.update { it.copy(prayerTimes = prayers, nextPrayer = next, isOnlineDataActive = isOnline) }
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

    val isOnline = _uiState.value.settings.useOnlineSchedule && _uiState.value.settings.cachedOnlineTimesJson.isNotBlank()

    _uiState.update {
      it.copy(
        prayerTimes = prayers,
        nextPrayer = next,
        timeUntilNextPrayer = timeRemainingStr,
        isOnlineDataActive = isOnline
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
    if (updated.useOnlineSchedule) {
      syncOnlinePrayerTimes(force = true)
    }
  }

  fun toggleUseOnlineSchedule(enabled: Boolean) {
    val current = _uiState.value.settings
    val updated = current.copy(useOnlineSchedule = enabled)
    updateSettings(updated)
    if (enabled) {
      syncOnlinePrayerTimes(force = true)
    }
  }

  fun syncOnlinePrayerTimes(force: Boolean = false, onFinished: ((Boolean, String) -> Unit)? = null) {
    val current = _uiState.value.settings
    if (!current.useOnlineSchedule && !force) return
    if (_uiState.value.isSyncingOnline) return

    viewModelScope.launch {
      _uiState.update { it.copy(isSyncingOnline = true, onlineSyncMessage = "Menghubungi server Kemenag RI...") }
      try {
        val now = Calendar.getInstance()
        val result = OnlinePrayerService.fetchPrayerTimes(
          latitude = current.latitude,
          longitude = current.longitude,
          calendar = now
        )

        if (result.isSuccess && result.timings.isNotEmpty()) {
          val json = JSONObject()
          result.timings.forEach { (type, timeStr) ->
            json.put(type.name, timeStr)
          }

          val timeFmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(now.time)
          val syncFmt = "$timeFmt WIB"

          val updated = _uiState.value.settings.copy(
            useOnlineSchedule = true,
            cachedOnlineTimesJson = json.toString(),
            cachedOnlineDate = result.dateKey,
            lastOnlineSyncFormatted = syncFmt,
            onlineSource = "Kemenag RI (${result.sourceName})"
          )
          updateSettings(updated)
          _uiState.update {
            it.copy(
              isSyncingOnline = false,
              isOnlineDataActive = true,
              onlineSyncMessage = "✅ Jadwal online resmi Kemenag RI (${current.cityName}) berhasil disinkronkan otomatis ($syncFmt). Tidak perlu mencocokkan jam secara manual."
            )
          }
          onFinished?.invoke(true, syncFmt)
        } else {
          val err = result.errorMessage ?: "Koneksi ke server gagal"
          val hasCache = current.cachedOnlineTimesJson.isNotBlank()
          _uiState.update {
            it.copy(
              isSyncingOnline = false,
              isOnlineDataActive = hasCache,
              onlineSyncMessage = if (hasCache) "Menggunakan cache online (${current.lastOnlineSyncFormatted})" else "Gagal sinkronisasi online ($err). Menggunakan hisab astronomi lokal."
            )
          }
          onFinished?.invoke(false, err)
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
            isSyncingOnline = false,
            onlineSyncMessage = "Kendala koneksi: ${e.message}"
          )
        }
        onFinished?.invoke(false, e.message ?: "Error")
      }
    }
  }

  fun autoDetectLocation(onFinished: ((Boolean, String) -> Unit)? = null) {
    viewModelScope.launch {
      _uiState.update { it.copy(isDetectingLocation = true) }
      try {
        val result = locationHelper.getCurrentOrBestLocation()
        val current = _uiState.value.settings
        val updated = current.copy(
          cityName = result.cityName,
          mosqueAddress = result.address,
          latitude = result.latitude,
          longitude = result.longitude,
          timezoneOffset = result.timezoneOffset
        )
        updateSettings(updated)
        if (updated.useOnlineSchedule) {
          syncOnlinePrayerTimes(force = true)
        }
        val msg = if (result.isGpsSuccess) {
          "Lokasi otomatis aktif: ${result.address} (${String.format(Locale.US, "%.4f, %.4f", result.latitude, result.longitude)})"
        } else {
          "Lokasi default digunakan: ${result.address}"
        }
        _uiState.update {
          it.copy(
            isDetectingLocation = false,
            locationDetectionMessage = msg
          )
        }
        onFinished?.invoke(result.isGpsSuccess, result.address)
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
            isDetectingLocation = false,
            locationDetectionMessage = "Gagal mendeteksi lokasi: ${e.message}"
          )
        }
        onFinished?.invoke(false, "Gagal: ${e.message}")
      }
    }
  }

  fun setBackgroundType(bgType: String) {
    val updated = _uiState.value.settings.copy(backgroundType = bgType)
    updateSettings(updated)
  }

  fun setTvLayoutTheme(theme: String) {
    val updated = _uiState.value.settings.copy(tvLayoutTheme = theme)
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

  fun togglePwaServer() {
    if (_uiState.value.isPwaServerRunning) {
      pwaServer.stopServer()
    } else {
      pwaServer.updateSettings(_uiState.value.settings)
      pwaServer.startServer()
    }
  }

  fun restartPwaServer() {
    pwaServer.stopServer()
    pwaServer.updateSettings(_uiState.value.settings)
    pwaServer.startServer()
  }

  fun setSubscriptionDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showSubscriptionDialog = visible) }
  }

  fun onScreencastClicked() {
    if (_uiState.value.isProSubscribed) {
      setCastGuideDialogVisible(true)
    } else {
      setSubscriptionDialogVisible(true)
    }
  }

  fun simulateToggleProSubscription() {
    val current = _uiState.value.isProSubscribed
    billingManager.setSubscriptionState(!current)
    if (!current) {
      setSubscriptionDialogVisible(false)
      setCastGuideDialogVisible(true)
    }
  }

  fun launchPlayStoreSubscription(activity: android.app.Activity) {
    val simOrderId = "GPA.${System.currentTimeMillis()}"
    billingManager.launchSubscription(activity) {
      billingManager.setSubscriptionState(true)
      setSubscriptionDialogVisible(false)
      _uiState.update {
        it.copy(
          currentOrderId = simOrderId,
          showPostPaymentRegistrationDialog = true
        )
      }
    }
  }

  fun setPostPaymentRegistrationDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showPostPaymentRegistrationDialog = visible) }
  }

  fun completeMosqueRegistration(
    mosqueName: String,
    cityName: String,
    mosqueAddress: String,
    dkmLeader: String,
    contactPhone: String,
    contactEmail: String
  ) {
    updateMosqueName(mosqueName)
    updateCityName(cityName)
    repository.updateSettings { it.copy(mosqueAddress = mosqueAddress) }

    viewModelScope.launch {
      val orderId = _uiState.value.currentOrderId.ifBlank { "GPA.${System.currentTimeMillis()}" }
      val updatedSettings = _uiState.value.settings.copy(
        mosqueName = mosqueName,
        cityName = cityName,
        mosqueAddress = mosqueAddress
      )
      subscriberRepository.recordOrUpdateSubscription(
        settings = updatedSettings,
        orderId = orderId,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        dkmLeaderName = dkmLeader
      )
      subscriberRepository.fetchAllSubscribers()
    }
  }

  fun restorePurchases() {
    billingManager.queryActivePurchases()
  }

  fun recordSubscriptionSuccess(orderId: String = "") {
    viewModelScope.launch {
      subscriberRepository.recordOrUpdateSubscription(
        settings = _uiState.value.settings,
        orderId = orderId
      )
    }
  }

  fun setAdminPanelVisible(visible: Boolean) {
    _uiState.update { it.copy(showAdminPanelDialog = visible) }
    if (visible) {
      refreshAdminSubscribers()
    }
  }

  fun setMosqueAccountDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showMosqueAccountDialog = visible) }
  }

  fun verifyAdminPin(pin: String): Boolean {
    return subscriberRepository.verifyAdminPin(pin)
  }

  fun changeAdminPin(newPin: String) {
    subscriberRepository.updateAdminPin(newPin)
  }

  fun refreshAdminSubscribers() {
    viewModelScope.launch {
      _uiState.update { it.copy(isAdminLoading = true) }
      val list = subscriberRepository.fetchAllSubscribers()
      val tickets = subscriberRepository.fetchSupportTickets()
      _uiState.update { it.copy(subscribersList = list, ticketsList = tickets, isAdminLoading = false) }
    }
  }

  fun adminResetDeviceBinding(subscriberId: String) {
    viewModelScope.launch {
      subscriberRepository.resetDeviceBinding(subscriberId)
      refreshAdminSubscribers()
    }
  }

  fun adminToggleProStatus(subscriberId: String, isPro: Boolean) {
    viewModelScope.launch {
      subscriberRepository.toggleProStatus(subscriberId, isPro)
      refreshAdminSubscribers()
    }
  }

  fun setPrivacyPolicyDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showPrivacyPolicyDialog = visible) }
  }

  fun setReportIssueDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showReportIssueDialog = visible) }
  }

  fun submitSupportTicket(category: String, contact: String, message: String) {
    viewModelScope.launch {
      subscriberRepository.submitSupportTicket(
        mosqueName = _uiState.value.settings.mosqueName,
        cityName = _uiState.value.settings.cityName,
        senderContact = contact,
        category = category,
        message = message
      )
    }
  }

  fun resolveSupportTicket(ticketId: String, isResolved: Boolean) {
    viewModelScope.launch {
      subscriberRepository.resolveSupportTicket(ticketId, isResolved)
    }
  }

  fun deleteSupportTicket(ticketId: String) {
    viewModelScope.launch {
      subscriberRepository.deleteSupportTicket(ticketId)
    }
  }

  override fun onCleared() {
    super.onCleared()
    pwaServer.stopServer()
    soundHelper.release()
  }
}
