package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MosqueDisplayState
import com.example.data.model.PrayerType
import com.example.ui.components.AdzanAlertOverlay
import com.example.ui.components.DigitalClockDisplay
import com.example.ui.components.IqomahCountdownOverlay
import com.example.ui.components.MarqueeTextBanner
import com.example.ui.components.MosqueBackground
import com.example.ui.components.MosqueInfoCarousel
import com.example.ui.components.PrayerTimeCard
import com.example.ui.components.SholatBlankScreen
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.SoftGray
import com.example.ui.viewmodel.MosqueUiState

@Composable
fun MosqueTvLandscapeScreen(
  uiState: MosqueUiState,
  onOpenSettings: () -> Unit,
  onOpenCastGuide: () -> Unit,
  onDismissSpecialState: () -> Unit,
  onStartIqomahNow: (PrayerType) -> Unit,
  onStartSholatNow: () -> Unit,
  modifier: Modifier = Modifier
) {
  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight
    val isCompactHeight = screenHeight < 450.dp

    MosqueBackground(
      backgroundType = uiState.settings.backgroundType,
      customBackgroundUri = uiState.settings.customBackgroundUri,
      overlayDarkness = uiState.settings.overlayDarkness,
      modifier = Modifier.fillMaxSize()
    ) {
      val headerContent: @Composable () -> Unit = {
        // TOP HEADER BAR
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (screenWidth < 600.dp) 10.dp else 20.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Mosque Identity & Mirroring Badge
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(if (isCompactHeight) 38.dp else 48.dp)
                .background(Emerald800, shape = CircleShape)
                .border(1.5.dp, Gold400, shape = CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = "Mosque Logo",
                tint = Gold400,
                modifier = Modifier.size(if (isCompactHeight) 20.dp else 26.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = uiState.settings.mosqueName,
                fontSize = if (isCompactHeight) 16.sp else 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gold400,
                letterSpacing = 1.sp,
                maxLines = 1
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = uiState.settings.mosqueAddress,
                  fontSize = if (isCompactHeight) 10.sp else 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = SoftGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Active TV Mirroring indicator
                Box(
                  modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .border(0.5.dp, Emerald500.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Emerald500, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TV MIRRORING", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                  }
                }
              }
            }
          }

          // Center: Next Prayer Countdown Badge
          if (uiState.nextPrayer != null) {
            Box(
              modifier = Modifier
                .background(
                  color = Obsidian900.copy(alpha = 0.85f),
                  shape = RoundedCornerShape(20.dp)
                )
                .border(
                  width = 1.5.dp,
                  color = Gold500.copy(alpha = 0.85f),
                  shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = if (isCompactHeight) 12.dp else 16.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "MENUJU ${uiState.nextPrayer.type.displayName.uppercase()}:",
                  fontSize = if (isCompactHeight) 11.sp else 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400,
                  modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                  text = "-${uiState.timeUntilNextPrayer}",
                  fontSize = if (isCompactHeight) 14.sp else 17.sp,
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace,
                  color = IvoryWhite
                )
              }
            }
          }

          // Right: Dates & Controls
          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = uiState.gregorianDate,
                fontSize = if (isCompactHeight) 12.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = IvoryWhite
              )
              Text(
                text = uiState.hijriDate,
                fontSize = if (isCompactHeight) 11.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Emerald500
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Switch to Mobile layout pill/button
            Box(
              modifier = Modifier
                .background(Obsidian900.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .border(1.dp, Gold400.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable { onOpenSettings() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("switch_to_mobile_view")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.PhoneAndroid,
                  contentDescription = "Mode HP",
                  tint = Gold400,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mode HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gold400)
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Screencast Button with PRO indicator
            Box(
              modifier = Modifier
                .background(
                  color = if (uiState.isProSubscribed) Emerald800 else Gold500,
                  shape = RoundedCornerShape(16.dp)
                )
                .border(
                  width = 1.dp,
                  color = if (uiState.isProSubscribed) Emerald500 else Gold400,
                  shape = RoundedCornerShape(16.dp)
                )
                .clickable { onOpenCastGuide() }
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .testTag("tv_screencast_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Cast,
                  contentDescription = "Screencast TV",
                  tint = if (uiState.isProSubscribed) IvoryWhite else Color.Black,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = if (uiState.isProSubscribed) "Cast Aktif" else "Screencast (Rp 10rb)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (uiState.isProSubscribed) IvoryWhite else Color.Black
                )
              }
            }
          }
        }
      }

      // SELECTABLE TV LAYOUT THEMES
      when (uiState.settings.tvLayoutTheme) {
        "vertical_sidebar" -> {
          VerticalSidebarTvLayout(
            uiState = uiState,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            headerContent = headerContent
          )
        }
        "center_dome" -> {
          CenterDomeTvLayout(
            uiState = uiState,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            headerContent = headerContent
          )
        }
        "cinematic_ambient" -> {
          CinematicAmbientTvLayout(
            uiState = uiState,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            headerContent = headerContent
          )
        }
        "grid_signage" -> {
          GridSignageTvLayout(
            uiState = uiState,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            headerContent = headerContent
          )
        }
        else -> {
          ModernSplitTvLayout(
            uiState = uiState,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            headerContent = headerContent
          )
        }
      }

      // Overlays for Adzan / Iqomah / Sholat
      when (uiState.displayState) {
        MosqueDisplayState.ADZAN -> {
          val prayerType = PrayerType.values().firstOrNull { it.displayName == uiState.specialStatePrayerName } ?: PrayerType.MAGHRIB
          AdzanAlertOverlay(
            prayerName = uiState.specialStatePrayerName,
            secondsLeft = uiState.iqomahSecondsLeft,
            onDismiss = onDismissSpecialState,
            onStartIqomahNow = { onStartIqomahNow(prayerType) }
          )
        }

        MosqueDisplayState.IQOMAH -> {
          IqomahCountdownOverlay(
            prayerName = uiState.specialStatePrayerName,
            secondsLeft = uiState.iqomahSecondsLeft,
            onDismiss = onDismissSpecialState,
            onStartSholatNow = onStartSholatNow
          )
        }

        MosqueDisplayState.SHOLAT_MODE -> {
          SholatBlankScreen(
            minutesLeft = uiState.sholatMinutesLeft,
            onDismiss = onDismissSpecialState
          )
        }

        MosqueDisplayState.NORMAL -> {
          // Nothing
        }
      }
    }
  }
}
