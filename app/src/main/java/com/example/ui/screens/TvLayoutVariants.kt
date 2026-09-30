package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerTimeItem
import com.example.ui.components.DigitalClockDisplay
import com.example.ui.components.MarqueeTextBanner
import com.example.ui.components.MosqueInfoCarousel
import com.example.ui.components.PrayerTimeCard
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.SoftGray
import com.example.ui.viewmodel.MosqueUiState

/**
 * TATA LETAK 1: MODERN SPLIT (Bawaan)
 * Jam kiri, Carousel kanan, 8 kartu sholat di bawah, Marquee di footer.
 */
@Composable
fun ModernSplitTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  headerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = if (isCompactHeight) 6.dp else 10.dp, bottom = 0.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    Spacer(modifier = Modifier.height(if (isCompactHeight) 4.dp else 8.dp))

    // Center Stage (Clock & Carousel)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = if (screenWidth < 600.dp) 10.dp else 20.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(modifier = Modifier.weight(1.2f)) {
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 52.sp else 68.sp,
          secondsFontSize = if (isCompactHeight) 18.sp else 24.sp,
          modifier = Modifier.fillMaxWidth()
        )
      }

      Box(modifier = Modifier.weight(1.3f)) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(if (isCompactHeight) 4.dp else 8.dp))

    // Bottom Prayer Cards Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = if (screenWidth < 600.dp) 8.dp else 20.dp),
      horizontalArrangement = Arrangement.spacedBy(if (screenWidth < 600.dp) 4.dp else 8.dp)
    ) {
      uiState.prayerTimes.forEach { item ->
        PrayerTimeCard(
          item = item,
          modifier = Modifier.weight(1f)
        )
      }
    }

    Spacer(modifier = Modifier.height(if (isCompactHeight) 4.dp else 8.dp))

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

/**
 * TATA LETAK 2: VERTICAL SIDEBAR
 * Sidebar kiri berisi Jam & list vertikal semua jadwal sholat.
 * Sisi kanan berisi Header masjid, Countdown besar, Carousel Info luas, & Marquee.
 */
@Composable
fun VerticalSidebarTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  headerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier.fillMaxSize()) {
    // SIDEBAR KIRI (Jam & Vertikal Prayer List)
    Box(
      modifier = Modifier
        .width(if (screenWidth < 700.dp) 210.dp else 260.dp)
        .fillMaxHeight()
        .background(Obsidian900.copy(alpha = 0.90f))
        .border(width = 1.dp, color = Gold500.copy(alpha = 0.35f))
        .padding(10.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Digital Clock Compact
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 38.sp else 46.sp,
          secondsFontSize = if (isCompactHeight) 14.sp else 18.sp,
          modifier = Modifier.fillMaxWidth()
        )

        // Vertical Prayer Times List
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 3.dp else 5.dp)
        ) {
          uiState.prayerTimes.forEach { prayer ->
            SidebarPrayerItemRow(prayer = prayer, isCompactHeight = isCompactHeight)
          }
        }

        // Date text at bottom of sidebar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = uiState.hijriDate, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald300)
          Text(text = uiState.gregorianDate, fontSize = 10.sp, color = SoftGray)
        }
      }
    }

    // MAIN CONTENT KANAN (Header, Big Countdown, Info Carousel, Marquee)
    Column(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .padding(top = 8.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      headerContent()

      // Big Next Prayer Countdown Banner
      if (uiState.nextPrayer != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(Emerald900.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
            .border(1.5.dp, Gold400, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = if (isCompactHeight) 6.dp else 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "MENUJU WAKTU ${uiState.nextPrayer.type.displayName.uppercase()}:",
              fontSize = if (isCompactHeight) 14.sp else 18.sp,
              fontWeight = FontWeight.Bold,
              color = Gold400
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "-${uiState.timeUntilNextPrayer}",
              fontSize = if (isCompactHeight) 24.sp else 34.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace,
              color = IvoryWhite
            )
          }
        }
      }

      // Large Info Carousel
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          modifier = Modifier.fillMaxSize()
        )
      }

      // Marquee Banner
      MarqueeTextBanner(
        texts = uiState.settings.runningTexts,
        velocityDp = uiState.settings.runningTextSpeed
      )
    }
  }
}

/**
 * TATA LETAK 3: CENTER DOME (Simetris Klasik)
 * Jam raksasa di tengah atas. Jadwal sholat simetris kiri 4 dan kanan 4.
 * Sangat ideal dan mudah dibaca dari jarak jauh/shaf belakang.
 */
@Composable
fun CenterDomeTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  headerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // Symmetrical Center Stage
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = if (screenWidth < 600.dp) 8.dp else 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Sisi Kiri: 4 Sholat Awal (Imsak, Subuh, Terbit, Dhuha)
      val leftPrayers = uiState.prayerTimes.take(uiState.prayerTimes.size / 2)
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        leftPrayers.forEach { prayer ->
          CompactPrayerBadge(prayer = prayer, isCompactHeight = isCompactHeight)
        }
      }

      // Tengah: Jam Raksasa Dome & Countdown
      Column(
        modifier = Modifier
          .weight(1.8f)
          .background(Obsidian900.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
          .border(2.dp, Gold400, RoundedCornerShape(20.dp))
          .padding(if (isCompactHeight) 8.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 56.sp else 74.sp,
          secondsFontSize = if (isCompactHeight) 20.sp else 26.sp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (uiState.nextPrayer != null) {
          Text(
            text = "Menuju ${uiState.nextPrayer.type.displayName}: -${uiState.timeUntilNextPrayer}",
            fontSize = if (isCompactHeight) 13.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            color = Gold400,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Sisi Kanan: 4 Sholat Akhir (Dzuhur, Ashar, Maghrib, Isya)
      val rightPrayers = uiState.prayerTimes.drop(uiState.prayerTimes.size / 2)
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        rightPrayers.forEach { prayer ->
          CompactPrayerBadge(prayer = prayer, isCompactHeight = isCompactHeight)
        }
      }
    }

    // Carousel Info di Bawah Center Dome
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = if (screenWidth < 600.dp) 8.dp else 16.dp, vertical = 2.dp)
    ) {
      MosqueInfoCarousel(
        settings = uiState.settings,
        activeSlideIndex = uiState.activeInfoSlideIndex,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

/**
 * TATA LETAK 4: CINEMATIC AMBIENT (Minimalis Elegan)
 * Tampilan transparan dengan glassmorphism floating badges, foto masjid dominan.
 */
@Composable
fun CinematicAmbientTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  headerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // Floating Ambient Center
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Floating Mosque Info Carousel
      Box(
        modifier = Modifier
          .weight(1.1f)
          .padding(end = 12.dp)
      ) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Right Floating Big Clock
      Box(
        modifier = Modifier
          .weight(1f)
          .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
          .border(1.5.dp, Gold400.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          DigitalClockDisplay(
            timeFormatted = uiState.timeFormatted,
            secondsFormatted = uiState.secondsFormatted,
            timeFontSize = if (isCompactHeight) 56.sp else 72.sp,
            secondsFontSize = if (isCompactHeight) 18.sp else 24.sp,
            modifier = Modifier.fillMaxWidth()
          )
          if (uiState.nextPrayer != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Menuju ${uiState.nextPrayer.type.displayName} -${uiState.timeUntilNextPrayer}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald300
            )
          }
        }
      }
    }

    // Floating Glass Prayer Bar at Bottom
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
        .border(1.dp, Gold500.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      uiState.prayerTimes.forEach { item ->
        PrayerTimeCard(item = item, modifier = Modifier.weight(1f))
      }
    }

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

/**
 * TATA LETAK 5: GRID SIGNAGE (Dashboard 4 Kuadran)
 * Menampilkan semua informasi sekaligus secara terstruktur.
 */
@Composable
fun GridSignageTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  headerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = 6.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // 4 Quadrants Grid
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Kuadran Kiri: Jam di atas, Carousel di bawah
      Column(
        modifier = Modifier.weight(1.1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Obsidian900.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
            .border(1.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          DigitalClockDisplay(
            timeFormatted = uiState.timeFormatted,
            secondsFormatted = uiState.secondsFormatted,
            timeFontSize = if (isCompactHeight) 44.sp else 58.sp,
            secondsFontSize = if (isCompactHeight) 16.sp else 20.sp,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          MosqueInfoCarousel(
            settings = uiState.settings,
            activeSlideIndex = uiState.activeInfoSlideIndex,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      // Kuadran Kanan: 8 Jadwal Sholat tersusun dalam 2 baris x 4 kolom
      Column(
        modifier = Modifier.weight(1.5f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val halfSize = uiState.prayerTimes.size / 2
        val topRowPrayers = uiState.prayerTimes.take(halfSize)
        val bottomRowPrayers = uiState.prayerTimes.drop(halfSize)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          topRowPrayers.forEach { item ->
            PrayerTimeCard(item = item, modifier = Modifier.weight(1f))
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          bottomRowPrayers.forEach { item ->
            PrayerTimeCard(item = item, modifier = Modifier.weight(1f))
          }
        }
      }
    }

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

@Composable
private fun SidebarPrayerItemRow(prayer: PrayerTimeItem, isCompactHeight: Boolean) {
  val isUpcoming = prayer.isUpcoming
  val bg = if (isUpcoming) Emerald800 else Obsidian800
  val border = if (isUpcoming) BorderStroke(1.5.dp, Gold400) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(bg, RoundedCornerShape(8.dp))
      .border(border, RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = if (isCompactHeight) 3.dp else 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = prayer.type.displayName,
      fontSize = if (isCompactHeight) 11.sp else 12.sp,
      fontWeight = if (isUpcoming) FontWeight.ExtraBold else FontWeight.Medium,
      color = if (isUpcoming) Gold400 else IvoryWhite
    )
    Text(
      text = prayer.timeString,
      fontSize = if (isCompactHeight) 12.sp else 14.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = if (isUpcoming) Gold400 else Emerald300
    )
  }
}

@Composable
private fun CompactPrayerBadge(prayer: PrayerTimeItem, isCompactHeight: Boolean) {
  val isUpcoming = prayer.isUpcoming
  val bg = if (isUpcoming) Emerald900 else Obsidian800
  val border = if (isUpcoming) BorderStroke(1.5.dp, Gold400) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(bg, RoundedCornerShape(10.dp))
      .border(border, RoundedCornerShape(10.dp))
      .padding(horizontal = 10.dp, vertical = if (isCompactHeight) 4.dp else 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = prayer.type.displayName,
      fontSize = if (isCompactHeight) 11.sp else 13.sp,
      fontWeight = if (isUpcoming) FontWeight.ExtraBold else FontWeight.SemiBold,
      color = if (isUpcoming) Gold400 else IvoryWhite
    )
    Text(
      text = prayer.timeString,
      fontSize = if (isCompactHeight) 13.sp else 15.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = if (isUpcoming) Gold400 else Emerald300
    )
  }
}
