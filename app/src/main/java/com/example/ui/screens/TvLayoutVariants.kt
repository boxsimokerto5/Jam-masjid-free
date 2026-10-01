package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
      .padding(top = if (isCompactHeight) 4.dp else 8.dp, bottom = 0.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // Center Stage (Clock & Carousel)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f, fill = true)
        .padding(
          horizontal = if (screenWidth < 600.dp) 8.dp else 16.dp,
          vertical = if (isCompactHeight) 2.dp else 6.dp
        ),
      horizontalArrangement = Arrangement.spacedBy(if (isCompactHeight) 10.dp else 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(1.2f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 42.sp else 62.sp,
          secondsFontSize = if (isCompactHeight) 15.sp else 22.sp,
          modifier = Modifier.fillMaxWidth()
        )
      }

      Box(
        modifier = Modifier
          .weight(1.3f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          isCompact = isCompactHeight,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // Bottom Prayer Cards Row (8 Sholat)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          horizontal = if (screenWidth < 600.dp) 6.dp else 14.dp,
          vertical = if (isCompactHeight) 2.dp else 4.dp
        ),
      horizontalArrangement = Arrangement.spacedBy(if (isCompactHeight) 4.dp else 6.dp)
    ) {
      uiState.prayerTimes.forEach { item ->
        PrayerTimeCard(
          item = item,
          isCompactHeight = isCompactHeight,
          modifier = Modifier.weight(1f)
        )
      }
    }

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

/**
 * TATA LETAK 2: VERTICAL SIDEBAR
 * Sidebar kiri dinaikkan dan dipercantik: Jam di atas, 8 sholat rapi tanpa terpotong, tanggal di bawah.
 * Sisi kanan: Header lengkap dengan tombol Mode HP & Miracast,
 * Area tengah megah diisi Carousel slide berganti (Laporan Kas, Kata Hikmah & Hadits, Petugas Jum'at),
 * dan Running Text di bagian bawah.
 */
@Composable
fun VerticalSidebarTvLayout(
  uiState: MosqueUiState,
  screenWidth: Dp,
  isCompactHeight: Boolean,
  onOpenSettings: () -> Unit = {},
  onOpenCastGuide: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier.fillMaxSize()) {
    // 1. SIDEBAR KIRI (Dinaikkan, kompak, jam di atas, 8 jadwal sholat muat sempurna)
    Box(
      modifier = Modifier
        .width(if (screenWidth < 700.dp) 195.dp else 235.dp)
        .fillMaxHeight()
        .background(Obsidian900.copy(alpha = 0.94f))
        .border(width = 1.dp, color = Gold500.copy(alpha = 0.4f))
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Digital Clock Compact & Upward
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 28.sp else 36.sp,
          secondsFontSize = if (isCompactHeight) 12.sp else 15.sp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 8 Jadwal Sholat Vertikal (Kompak, tidak terpotong, Isya' tampil utuh)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
          uiState.prayerTimes.forEach { prayer ->
            SidebarPrayerItemRow(prayer = prayer, isCompactHeight = isCompactHeight)
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Tanggal Hijriyah & Masehi di bawah sidebar
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(bottom = 2.dp)
        ) {
          Text(
            text = uiState.hijriDate,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald300,
            maxLines = 1
          )
          Text(
            text = uiState.gregorianDate,
            fontSize = 8.5.sp,
            color = SoftGray,
            maxLines = 1
          )
        }
      }
    }

    // 2. MAIN CONTENT KANAN (Header khusus tanpa overflow, Center Carousel luas, Marquee)
    Column(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .padding(top = 4.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Header Khusus Area Kanan (Tombol Mode HP & Miracast selalu tampil utuh!)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Identitas Masjid
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(if (isCompactHeight) 32.dp else 38.dp)
              .background(Emerald800, shape = CircleShape)
              .border(1.dp, Gold400, shape = CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Mosque,
              contentDescription = "Mosque Logo",
              tint = Gold400,
              modifier = Modifier.size(if (isCompactHeight) 18.dp else 22.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = uiState.settings.mosqueName,
              fontSize = if (isCompactHeight) 14.sp else 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Gold400,
              letterSpacing = 0.5.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = uiState.settings.mosqueAddress,
                fontSize = if (isCompactHeight) 9.sp else 11.sp,
                color = SoftGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                  .border(0.5.dp, Emerald500.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text("TV", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Emerald300)
              }
              if (uiState.isOnlineDataActive) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                    .border(0.5.dp, Gold400, RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text("KEMENAG", fontSize = 7.5.sp, fontWeight = FontWeight.ExtraBold, color = Gold400)
                }
              }
            }
          }
        }

        // Action Buttons: Countdown Badge + Mode HP + Miracast
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (uiState.nextPrayer != null) {
            Box(
              modifier = Modifier
                .background(Obsidian900.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                .border(1.dp, Gold500.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "MENUJU ${uiState.nextPrayer.type.displayName.uppercase()}:",
                  fontSize = if (isCompactHeight) 9.5.sp else 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400,
                  modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                  text = uiState.timeUntilNextPrayer,
                  fontSize = if (isCompactHeight) 12.sp else 14.sp,
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace,
                  color = IvoryWhite
                )
              }
            }
          }

          // Tombol Kembali ke Mode HP
          Box(
            modifier = Modifier
              .background(Obsidian900.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
              .border(1.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .clickable { onOpenSettings() }
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("sidebar_mode_hp_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = "Mode HP",
                tint = Gold400,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text("Mode HP", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Gold400)
            }
          }

          // Tombol Miracast / Screencast TV
          Box(
            modifier = Modifier
              .background(if (uiState.isProSubscribed) Emerald800 else Gold500, RoundedCornerShape(12.dp))
              .border(1.dp, if (uiState.isProSubscribed) Emerald500 else Gold400, RoundedCornerShape(12.dp))
              .clickable { onOpenCastGuide() }
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("sidebar_miracast_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Cast,
                contentDescription = "Miracast",
                tint = if (uiState.isProSubscribed) IvoryWhite else Color.Black,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (uiState.isProSubscribed) "Miracast On" else "Miracast",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (uiState.isProSubscribed) IvoryWhite else Color.Black
              )
            }
          }
        }
      }

      // 3. TENGAH HERO STAGE: CAROUSEL SLIDE BERGANTI (Kas, Kata Hikmah & Hadits, Petugas Jumat)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          isCompact = false,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 4. FOOTER: Running Text Marquee
      MarqueeTextBanner(
        texts = uiState.settings.runningTexts,
        velocityDp = uiState.settings.runningTextSpeed
      )
    }
  }
}

/**
 * TATA LETAK 3: CENTER DOME (Simetris Klasik)
 * Jam raksasa di tengah. Jadwal sholat simetris kiri 4 dan kanan 4.
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
      .padding(top = if (isCompactHeight) 4.dp else 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // Symmetrical Center Stage
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(
          horizontal = if (screenWidth < 600.dp) 6.dp else 14.dp,
          vertical = if (isCompactHeight) 2.dp else 4.dp
        ),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Sisi Kiri: 4 Sholat Awal (Imsak, Subuh, Syuruq, Dhuha)
      val leftPrayers = uiState.prayerTimes.take(uiState.prayerTimes.size / 2)
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 3.dp else 5.dp)
      ) {
        leftPrayers.forEach { prayer ->
          CompactPrayerBadge(prayer = prayer, isCompactHeight = isCompactHeight)
        }
      }

      // Tengah: Jam Dome & Countdown & Carousel
      Column(
        modifier = Modifier
          .weight(1.8f)
          .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
      ) {
        DigitalClockDisplay(
          timeFormatted = uiState.timeFormatted,
          secondsFormatted = uiState.secondsFormatted,
          timeFontSize = if (isCompactHeight) 42.sp else 60.sp,
          secondsFontSize = if (isCompactHeight) 15.sp else 20.sp,
          modifier = Modifier.fillMaxWidth()
        )

        if (uiState.nextPrayer != null) {
          Box(
            modifier = Modifier
              .background(Obsidian900.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
              .border(1.dp, Gold400, RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 2.dp)
          ) {
            Text(
              text = "Menuju ${uiState.nextPrayer.type.displayName}: ${uiState.timeUntilNextPrayer}",
              fontSize = if (isCompactHeight) 12.sp else 14.sp,
              fontWeight = FontWeight.Bold,
              color = Gold400,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          isCompact = isCompactHeight,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Sisi Kanan: 4 Sholat Akhir (Dzuhur, Ashar, Maghrib, Isya)
      val rightPrayers = uiState.prayerTimes.drop(uiState.prayerTimes.size / 2)
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 3.dp else 5.dp)
      ) {
        rightPrayers.forEach { prayer ->
          CompactPrayerBadge(prayer = prayer, isCompactHeight = isCompactHeight)
        }
      }
    }

    MarqueeTextBanner(
      texts = uiState.settings.runningTexts,
      velocityDp = uiState.settings.runningTextSpeed
    )
  }
}

/**
 * TATA LETAK 4: CINEMATIC AMBIENT (Minimalis Elegan)
 * Tampilan transparan dengan floating badges, foto masjid dominan dan megah.
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
      .padding(top = if (isCompactHeight) 4.dp else 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // Floating Ambient Center
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(
          horizontal = if (screenWidth < 600.dp) 8.dp else 18.dp,
          vertical = if (isCompactHeight) 2.dp else 6.dp
        ),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Floating Mosque Info Carousel
      Box(
        modifier = Modifier
          .weight(1.1f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          isCompact = isCompactHeight,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Right Floating Big Clock
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          DigitalClockDisplay(
            timeFormatted = uiState.timeFormatted,
            secondsFormatted = uiState.secondsFormatted,
            timeFontSize = if (isCompactHeight) 44.sp else 62.sp,
            secondsFontSize = if (isCompactHeight) 15.sp else 22.sp,
            modifier = Modifier.fillMaxWidth()
          )
          if (uiState.nextPrayer != null) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                .border(0.8.dp, Gold400, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Menuju ${uiState.nextPrayer.type.displayName}: ${uiState.timeUntilNextPrayer}",
                fontSize = if (isCompactHeight) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                color = Gold400,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }

    // Floating Glass Prayer Bar at Bottom
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          horizontal = if (screenWidth < 600.dp) 6.dp else 12.dp,
          vertical = if (isCompactHeight) 2.dp else 4.dp
        )
        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
        .border(1.dp, Gold500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .padding(horizontal = 6.dp, vertical = if (isCompactHeight) 2.dp else 4.dp),
      horizontalArrangement = Arrangement.spacedBy(if (isCompactHeight) 4.dp else 6.dp)
    ) {
      uiState.prayerTimes.forEach { item ->
        PrayerTimeCard(
          item = item,
          isCompactHeight = isCompactHeight,
          modifier = Modifier.weight(1f)
        )
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
      .padding(top = if (isCompactHeight) 4.dp else 6.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    headerContent()

    // 4 Quadrants Grid
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 10.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Kuadran Kiri: Jam di atas, Carousel di bawah
      Column(
        modifier = Modifier
          .weight(1.1f)
          .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          DigitalClockDisplay(
            timeFormatted = uiState.timeFormatted,
            secondsFormatted = uiState.secondsFormatted,
            timeFontSize = if (isCompactHeight) 38.sp else 50.sp,
            secondsFontSize = if (isCompactHeight) 14.sp else 18.sp,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          MosqueInfoCarousel(
            settings = uiState.settings,
            activeSlideIndex = uiState.activeInfoSlideIndex,
            isCompact = true,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      // Kuadran Kanan: 8 Jadwal Sholat tersusun dalam 2 baris x 4 kolom
      Column(
        modifier = Modifier
          .weight(1.5f)
          .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val halfSize = uiState.prayerTimes.size / 2
        val topRowPrayers = uiState.prayerTimes.take(halfSize)
        val bottomRowPrayers = uiState.prayerTimes.drop(halfSize)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          topRowPrayers.forEach { item ->
            PrayerTimeCard(item = item, isCompactHeight = true, modifier = Modifier.weight(1f))
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          bottomRowPrayers.forEach { item ->
            PrayerTimeCard(item = item, isCompactHeight = true, modifier = Modifier.weight(1f))
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
      .background(bg, RoundedCornerShape(5.dp))
      .border(border, RoundedCornerShape(5.dp))
      .padding(horizontal = 6.dp, vertical = if (isCompactHeight) 1.dp else 2.5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = prayer.type.displayName,
      fontSize = if (isCompactHeight) 10.sp else 11.5.sp,
      fontWeight = if (isUpcoming) FontWeight.ExtraBold else FontWeight.Medium,
      color = if (isUpcoming) Gold400 else IvoryWhite
    )
    Text(
      text = prayer.timeString,
      fontSize = if (isCompactHeight) 11.sp else 13.sp,
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
      .background(bg, RoundedCornerShape(8.dp))
      .border(border, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = if (isCompactHeight) 3.dp else 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = prayer.type.displayName,
      fontSize = if (isCompactHeight) 10.5.sp else 12.5.sp,
      fontWeight = if (isUpcoming) FontWeight.ExtraBold else FontWeight.SemiBold,
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
