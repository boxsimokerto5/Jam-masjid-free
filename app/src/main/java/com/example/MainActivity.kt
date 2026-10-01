package com.example

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.model.MosqueDisplayState
import com.example.ui.dialogs.CastGuideDialog
import com.example.ui.dialogs.MosqueAccountDialog
import com.example.ui.dialogs.MosqueAdminPanelDialog
import com.example.ui.dialogs.MosqueSubscriptionDialog
import com.example.ui.dialogs.PrivacyPolicyDialog
import com.example.ui.dialogs.ReportIssueDialog
import com.example.ui.screens.MosqueMobilePortraitScreen
import com.example.ui.screens.MosqueTvLandscapeScreen
import com.example.ui.theme.MosqueClockTheme
import com.example.ui.viewmodel.MosqueClockViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: MosqueClockViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Keep screen on continuously for Mosque Display
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    setContent {
      MosqueClockTheme {
        val uiState by viewModel.uiState.collectAsState()
        val configuration = LocalConfiguration.current
        val isPhysicalLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Responsive dynamic orientation: true if phone is physically in landscape OR if user toggled TV mode
        val showTvDashboard = isPhysicalLandscape || uiState.isForcedTvMode

        // Immersive TV full-screen mode on landscape / TV mirroring
        val view = LocalView.current
        LaunchedEffect(showTvDashboard) {
          val window = this@MainActivity.window
          val insetsController = WindowCompat.getInsetsController(window, view)
          if (showTvDashboard) {
            insetsController.systemBarsBehavior =
              WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
          } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
          }
        }

        // Handle Back button
        BackHandler(enabled = uiState.isForcedTvMode || uiState.displayState != MosqueDisplayState.NORMAL) {
          if (uiState.displayState != MosqueDisplayState.NORMAL) {
            viewModel.dismissSpecialState()
          } else if (uiState.isForcedTvMode) {
            viewModel.toggleForcedTvMode()
          }
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .then(if (showTvDashboard) Modifier else Modifier.safeDrawingPadding())
        ) {
          // Animated responsive layout switch between Vertical Mobile feed and Horizontal TV dashboard
          AnimatedContent(
            targetState = showTvDashboard,
            transitionSpec = {
              fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(250))
            },
            label = "responsive_orientation_transition"
          ) { isTvMode ->
            if (isTvMode) {
              // FULL-SCREEN HORIZONTAL DASHBOARD FOR TV MIRRORING
              MosqueTvLandscapeScreen(
                uiState = uiState,
                onOpenSettings = {
                  if (uiState.isForcedTvMode) {
                    viewModel.toggleForcedTvMode()
                  }
                },
                onOpenCastGuide = {
                  viewModel.onScreencastClicked()
                },
                onDismissSpecialState = {
                  viewModel.dismissSpecialState()
                },
                onStartIqomahNow = { prayer ->
                  viewModel.startIqomahCountdown(prayer)
                },
                onStartSholatNow = {
                  viewModel.startSholatMode()
                },
                modifier = Modifier.fillMaxSize()
              )
            } else {
              // VERTICAL INFORMATION FEED & REMOTE CONTROL FOR MOBILE
              MosqueMobilePortraitScreen(
                uiState = uiState,
                onToggleTvMode = {
                  viewModel.toggleForcedTvMode()
                },
                onOpenCastGuide = {
                  viewModel.onScreencastClicked()
                },
                onUpdateSettings = { newSettings ->
                  viewModel.updateSettings(newSettings)
                },
                onSelectCity = { city ->
                  viewModel.selectCity(city)
                },
                onAutoDetectLocation = {
                  viewModel.autoDetectLocation()
                },
                onToggleUseOnlineSchedule = { enabled ->
                  viewModel.toggleUseOnlineSchedule(enabled)
                },
                onSyncOnlineNow = {
                  viewModel.syncOnlinePrayerTimes(force = true)
                },
                onSetBackgroundType = { bgType ->
                  viewModel.setBackgroundType(bgType)
                },
                onSetCustomBackgroundUri = { uriStr ->
                  viewModel.setCustomBackgroundUri(uriStr)
                },
                onSetOverlayDarkness = { darkness ->
                  viewModel.setOverlayDarkness(darkness)
                },
                onSetTvLayoutTheme = { theme ->
                  viewModel.setTvLayoutTheme(theme)
                },
                onAddRunningText = { text ->
                  viewModel.addRunningText(text)
                },
                onRemoveRunningText = { idx ->
                  viewModel.removeRunningText(idx)
                },
                onTestAdzan = { prayer ->
                  viewModel.triggerAdzan(prayer)
                },
                onTestIqomah = { prayer, seconds ->
                  viewModel.startIqomahCountdown(prayer, seconds)
                },
                onTestSholatMode = { minutes ->
                  viewModel.startSholatMode(minutes)
                },
                onOpenAdminPanel = {
                  viewModel.setAdminPanelVisible(true)
                },
                onOpenAccountDialog = {
                  viewModel.setMosqueAccountDialogVisible(true)
                },
                onOpenPrivacyPolicy = {
                  viewModel.setPrivacyPolicyDialogVisible(true)
                },
                onOpenReportIssue = {
                  viewModel.setReportIssueDialogVisible(true)
                },
                modifier = Modifier.fillMaxSize()
              )
            }
          }

          // Super Admin Panel Dialog (Monitoring Pelanggan & Lokasi Masjid)
          if (uiState.showAdminPanelDialog) {
            MosqueAdminPanelDialog(
              subscribers = uiState.subscribersList,
              tickets = uiState.ticketsList,
              isLoading = uiState.isAdminLoading,
              onVerifyPin = { pin -> viewModel.verifyAdminPin(pin) },
              onResetDeviceBinding = { subId -> viewModel.adminResetDeviceBinding(subId) },
              onTogglePro = { subId, isPro -> viewModel.adminToggleProStatus(subId, isPro) },
              onResolveTicket = { ticketId, isRes -> viewModel.resolveSupportTicket(ticketId, isRes) },
              onDeleteTicket = { ticketId -> viewModel.deleteSupportTicket(ticketId) },
              onRefreshData = {
                viewModel.refreshAdminSubscribers()
              },
              onChangePin = { newPin -> viewModel.changeAdminPin(newPin) },
              onDismiss = { viewModel.setAdminPanelVisible(false) }
            )
          }

          // Mosque Account & Device Binding Status Dialog
          if (uiState.showMosqueAccountDialog) {
            MosqueAccountDialog(
              settings = uiState.settings,
              isProSubscribed = uiState.isProSubscribed,
              deviceId = uiState.currentDeviceId,
              deviceModel = uiState.currentDeviceModel,
              onOpenSubscription = {
                viewModel.setSubscriptionDialogVisible(true)
              },
              onOpenPrivacyPolicy = {
                viewModel.setPrivacyPolicyDialogVisible(true)
              },
              onOpenReportIssue = {
                viewModel.setReportIssueDialogVisible(true)
              },
              onDismiss = {
                viewModel.setMosqueAccountDialogVisible(false)
              }
            )
          }

          // Kebijakan Privasi Dialog (Google Play Privacy Policy)
          if (uiState.showPrivacyPolicyDialog) {
            PrivacyPolicyDialog(
              onDismiss = {
                viewModel.setPrivacyPolicyDialogVisible(false)
              }
            )
          }

          // Pusat Bantuan & Lapor Kendala Dialog (Masuk ke Super Admin)
          if (uiState.showReportIssueDialog) {
            ReportIssueDialog(
              settings = uiState.settings,
              deviceId = uiState.currentDeviceId,
              deviceModel = uiState.currentDeviceModel,
              onSubmitTicket = { category, contact, message ->
                viewModel.submitSupportTicket(category, contact, message)
              },
              onDismiss = {
                viewModel.setReportIssueDialogVisible(false)
              }
            )
          }

          // Google Play In-App Subscription Dialog (Rp 10.000 / month)
          if (uiState.showSubscriptionDialog) {
            MosqueSubscriptionDialog(
              isSubscribed = uiState.isProSubscribed,
              onDismiss = {
                viewModel.setSubscriptionDialogVisible(false)
              },
              onSubscribeClicked = { activity ->
                viewModel.launchPlayStoreSubscription(activity)
              },
              onSimulateUnlock = {
                viewModel.simulateToggleProSubscription()
              },
              onRestorePurchases = {
                viewModel.restorePurchases()
              }
            )
          }

          // Miracast / Cast Guide Dialog
          if (uiState.showCastGuideDialog) {
            CastGuideDialog(
              pwaServerUrl = uiState.pwaServerUrl,
              isPwaServerRunning = uiState.isPwaServerRunning,
              onDismiss = {
                viewModel.setCastGuideDialogVisible(false)
              },
              onForceTvMode = {
                if (!uiState.isForcedTvMode) {
                  viewModel.toggleForcedTvMode()
                }
              }
            )
          }
        }
      }
    }
  }
}
