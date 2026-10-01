package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CityData
import com.example.data.model.CityPreset
import com.example.data.model.MosqueSettings
import com.example.data.model.PrayerTimeItem
import com.example.data.model.PrayerType
import com.example.ui.components.DigitalClockDisplay
import com.example.ui.components.MarqueeTextBanner
import com.example.ui.components.MosqueInfoCarousel
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray
import com.example.ui.viewmodel.MosqueUiState

@Composable
fun MosqueMobilePortraitScreen(
  uiState: MosqueUiState,
  onToggleTvMode: () -> Unit,
  onOpenCastGuide: () -> Unit,
  onUpdateSettings: (MosqueSettings) -> Unit,
  onSelectCity: (CityPreset) -> Unit,
  onAutoDetectLocation: () -> Unit,
  onToggleUseOnlineSchedule: (Boolean) -> Unit = {},
  onSyncOnlineNow: () -> Unit = {},
  onSetBackgroundType: (String) -> Unit,
  onSetCustomBackgroundUri: (String) -> Unit,
  onSetOverlayDarkness: (Float) -> Unit,
  onSetTvLayoutTheme: (String) -> Unit,
  onAddRunningText: (String) -> Unit,
  onRemoveRunningText: (Int) -> Unit,
  onTestAdzan: (PrayerType) -> Unit,
  onTestIqomah: (PrayerType, Int) -> Unit,
  onTestSholatMode: (Int) -> Unit,
  onOpenAdminPanel: () -> Unit = {},
  onOpenAccountDialog: () -> Unit = {},
  onOpenPrivacyPolicy: () -> Unit = {},
  onOpenReportIssue: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabTitles = listOf("🎨 Tampilan", "🕌 Jadwal", "⏱️ Iqomah", "📜 Running Text", "💰 Kas & Info", "🛡️ Admin & Lisensi")

  // PhotoPicker for custom background
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onSetCustomBackgroundUri(uri.toString())
    }
  }

  val context = LocalContext.current
  var citySearchQuery by remember { mutableStateOf("") }

  // Location Permission Launcher
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      onAutoDetectLocation()
    }
  }

  fun requestAutoLocation() {
    val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
      onAutoDetectLocation()
    } else {
      locationPermissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        )
      )
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Obsidian950)
  ) {
    // 1. TOP BAR: Branding & Quick Mirroring Action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Obsidian900)
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(Emerald800, CircleShape)
            .border(1.dp, Gold400, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Mosque, contentDescription = "Logo", tint = Gold400, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(uiState.settings.mosqueName, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Gold400, maxLines = 1)
          Text(uiState.settings.mosqueAddress, fontSize = 11.sp, color = SoftGray, maxLines = 1)
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Device Binding & Account Button
        IconButton(
          onClick = onOpenAccountDialog,
          modifier = Modifier
            .size(36.dp)
            .background(Obsidian800, CircleShape)
            .testTag("device_binding_header_button")
        ) {
          Icon(Icons.Default.Devices, contentDescription = "Status Perangkat & Lisensi", tint = Emerald300, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Admin Panel Button (Monitoring Lokasi & Langganan)
        IconButton(
          onClick = onOpenAdminPanel,
          modifier = Modifier
            .size(36.dp)
            .background(Emerald800.copy(alpha = 0.9f), CircleShape)
            .border(1.dp, Gold400, CircleShape)
            .testTag("open_admin_panel_button")
        ) {
          Icon(Icons.Default.Security, contentDescription = "Panel Super Admin", tint = Gold400, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Cast Guide Button
        IconButton(
          onClick = onOpenCastGuide,
          modifier = Modifier
            .size(36.dp)
            .background(Emerald800.copy(alpha = 0.8f), CircleShape)
            .testTag("cast_to_tv_header")
        ) {
          Icon(Icons.Default.Cast, contentDescription = "Cast ke TV", tint = Gold400, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Sound Toggle
        IconButton(
          onClick = {
            onUpdateSettings(uiState.settings.copy(isSoundAlertEnabled = !uiState.settings.isSoundAlertEnabled))
          },
          modifier = Modifier
            .size(36.dp)
            .background(Obsidian800, CircleShape)
        ) {
          Icon(
            imageVector = if (uiState.settings.isSoundAlertEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
            contentDescription = "Suara",
            tint = if (uiState.settings.isSoundAlertEnabled) Emerald500 else SoftGray,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 2. DYNAMIC ORIENTATION BANNER (Interactive Mode Switcher)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp),
      colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.9f)),
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, Gold500.copy(alpha = 0.5f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Mode HP Vertikal Aktif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            Text("Putar HP atau ketuk tombol untuk tampilan TV", fontSize = 10.sp, color = Emerald300)
          }
        }

        Button(
          onClick = onToggleTvMode,
          colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier
            .height(30.dp)
            .testTag("launch_tv_dashboard")
        ) {
          Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Dashboard TV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 3. VERTICAL INFORMATION SCROLL FEED
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Hero Digital Clock Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Obsidian900),
          border = BorderStroke(1.5.dp, Gold500.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = uiState.gregorianDate,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = SoftGray
            )
            Text(
              text = uiState.hijriDate,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald500
            )

            Spacer(modifier = Modifier.height(8.dp))

            DigitalClockDisplay(
              timeFormatted = uiState.timeFormatted,
              secondsFormatted = uiState.secondsFormatted,
              timeFontSize = 46.sp,
              secondsFontSize = 18.sp,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Next Prayer Banner
            if (uiState.nextPrayer != null) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Emerald800.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                  .border(1.dp, Gold400.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                  .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = "MENUJU ${uiState.nextPrayer.type.displayName.uppercase()}:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold400
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "-${uiState.timeUntilNextPrayer}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = IvoryWhite
                  )
                }
              }
            }
          }
        }
      }

      // Vertical Prayer Times Schedule (All 8 Prayers in Vertical Cards)
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Obsidian900),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Jadwal Sholat Hari Ini (Vertikal)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
              }
              Text(uiState.settings.cityName, fontSize = 12.sp, color = Emerald500, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            uiState.prayerTimes.forEach { prayer ->
              VerticalPrayerRowItem(prayer = prayer)
              Spacer(modifier = Modifier.height(6.dp))
            }
          }
        }
      }

      // Live Information Carousel (Kas, Petugas Jum'at, Hadits)
      item {
        MosqueInfoCarousel(
          settings = uiState.settings,
          activeSlideIndex = uiState.activeInfoSlideIndex,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Running Text Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black),
          border = BorderStroke(1.dp, Gold400.copy(alpha = 0.3f))
        ) {
          MarqueeTextBanner(
            texts = uiState.settings.runningTexts,
            velocityDp = uiState.settings.runningTextSpeed
          )
        }
      }

      // 4. REMOTE CONTROL & CUSTOMIZATION TABS
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Panel Pengaturan & Remote TV",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Gold400
        )
      }

      item {
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = Obsidian900,
          contentColor = Gold400,
          edgePadding = 0.dp
        ) {
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) Gold400 else SoftGray
                )
              }
            )
          }
        }
      }

      // TAB CONTENTS
      when (selectedTab) {
        0 -> {
          // Tab 0: Tampilan, Tata Letak TV & Background
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.85f)),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.5.dp, Gold400.copy(alpha = 0.7f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Pilihan Tata Letak (Tema Layar TV)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold400)
                    Text("Ubah posisi jam, jadwal sholat, dan info di TV", fontSize = 11.sp, color = IvoryWhite)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val layouts = listOf(
                  Triple("modern_split", "1. Modern Split (Bawaan)", "Jam di kiri, info di kanan, 8 sholat di bawah"),
                  Triple("vertical_sidebar", "2. Vertical Sidebar", "Sidebar jam & jadwal sholat di kiri, area info luas di kanan"),
                  Triple("center_dome", "3. Center Dome (Simetris)", "Jam raksasa di tengah, sholat simetris 4 kiri & 4 kanan"),
                  Triple("cinematic_ambient", "4. Cinematic Ambient", "Efek floating transparan, foto masjid tampil maksimal"),
                  Triple("grid_signage", "5. Grid Signage (Dashboard)", "Format 4 kuadran terstruktur, semua info tampil bersamaan")
                )

                layouts.forEach { (themeKey, title, desc) ->
                  val isSelected = uiState.settings.tvLayoutTheme == themeKey
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                      .background(
                        color = if (isSelected) Gold500 else Obsidian800,
                        shape = RoundedCornerShape(10.dp)
                      )
                      .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Gold400 else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                      )
                      .clickable { onSetTvLayoutTheme(themeKey) }
                      .padding(horizontal = 12.dp, vertical = 10.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = title,
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) Color.Black else IvoryWhite
                        )
                        Text(
                          text = desc,
                          fontSize = 11.sp,
                          color = if (isSelected) Color.Black.copy(alpha = 0.75f) else SoftGray
                        )
                      }
                      if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = "Terpilih", tint = Color.Black)
                      }
                    }
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenCastGuide() },
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier.size(36.dp).background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Sambungkan ke Layar TV Masjid", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                    Text(
                      text = if (uiState.pwaServerUrl.isNotBlank()) "PWA Offline: ${uiState.pwaServerUrl} (HP bisa dicabut/dibawa pulang)" else "Miracast & Web Server Offline TV",
                      fontSize = 11.sp,
                      color = Emerald300
                    )
                  }
                }
                Icon(Icons.Default.Cast, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Pilihan Gambar & Warna Background TV:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gold400)
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(modifier = Modifier.size(36.dp).background(Emerald800, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Gunakan Foto Sendiri", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                    Text("Pilih foto masjid dari Galeri HP", fontSize = 11.sp, color = SoftGray)
                  }
                }
                Button(
                  onClick = {
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.Black)
                ) {
                  Text("Pilih Foto", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }

          val presets = listOf(
            Triple("preset_twilight", "Foto Masjid Senja (Twilight)", "Kubah megah & pantulan air kebiruan"),
            Triple("preset_emerald", "Foto Interior Masjid Emerald", "Arsitektur kubah & lentera emas"),
            Triple("gradient_emerald", "Corak Gradien Hijau Zamrud", "Nuansa Islami mewah & tenang"),
            Triple("gradient_midnight", "Corak Gradien Biru Malam", "Elegan bernuansa langit malam"),
            Triple("gradient_sunset", "Corak Gradien Amber Sunset", "Hangat bernuansa senja"),
            Triple("gradient_dark", "Hitam Karbon Minimalis (OLED)", "Kontras tinggi hemat daya")
          )

          items(presets) { (type, label, desc) ->
            val isSelected = uiState.settings.backgroundType == type
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSetBackgroundType(type) },
              colors = CardDefaults.cardColors(containerColor = if (isSelected) Emerald900 else Obsidian800),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, if (isSelected) Gold400 else Color.White.copy(alpha = 0.08f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Gold400 else IvoryWhite)
                  Text(desc, fontSize = 11.sp, color = SoftGray)
                }
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = "Terpilih", tint = Gold400)
                }
              }
            }
          }

          // Darkness Slider
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Kegelapan Lapisan Latar (Overlay):", fontSize = 12.sp, color = IvoryWhite, fontWeight = FontWeight.SemiBold)
                  Text("${(uiState.settings.overlayDarkness * 100).toInt()}%", fontSize = 12.sp, color = Gold400, fontWeight = FontWeight.Bold)
                }
                Slider(
                  value = uiState.settings.overlayDarkness,
                  onValueChange = onSetOverlayDarkness,
                  valueRange = 0.15f..0.85f,
                  colors = SliderDefaults.colors(thumbColor = Gold400, activeTrackColor = Gold500)
                )
              }
            }
          }
        }

        1 -> {
          // Tab 1: Jadwal Online Kemenag & Lokasi Otomatis
          // 1. SINKRONISASI JADWAL ONLINE RESMI KEMENAG RI
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.95f)),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.5.dp, Gold400)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                // Header with Switch
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(42.dp)
                        .background(Emerald800, CircleShape)
                        .border(1.2.dp, Gold400, CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Gold400,
                        modifier = Modifier.size(22.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "Ambil Jadwal dari Data Online",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Gold400
                      )
                      Text(
                        text = "Resmi Kementerian Agama RI (Kemenag)",
                        fontSize = 11.sp,
                        color = IvoryWhite
                      )
                    }
                  }

                  Switch(
                    checked = uiState.settings.useOnlineSchedule,
                    onCheckedChange = { onToggleUseOnlineSchedule(it) },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = Gold400,
                      checkedTrackColor = Emerald500,
                      uncheckedThumbColor = SoftGray,
                      uncheckedTrackColor = Obsidian800
                    )
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Box when online is enabled
                if (uiState.settings.useOnlineSchedule) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                      .border(1.dp, Gold400.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                      .padding(12.dp)
                  ) {
                    Column {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          if (uiState.isSyncingOnline) {
                            CircularProgressIndicator(
                              modifier = Modifier.size(14.dp),
                              color = Gold400,
                              strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = "Menghubungi server Kemenag...",
                              fontSize = 11.sp,
                              color = Gold400,
                              fontWeight = FontWeight.Bold
                            )
                          } else {
                            Icon(
                              imageVector = Icons.Default.CheckCircle,
                              contentDescription = null,
                              tint = Emerald500,
                              modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = "Data Online Aktif (100% Otomatis)",
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              color = Emerald300
                            )
                          }
                        }

                        // Badge
                        Box(
                          modifier = Modifier
                            .background(Gold500, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = "KEMENAG RI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                          )
                        }
                      }

                      Spacer(modifier = Modifier.height(8.dp))

                      Text(
                        text = "Jam Subuh, Syuruq, Dzuhur, Ashar, Maghrib, dan Isya otomatis ditarik dari data online resmi Kemenag RI untuk wilayah ${uiState.settings.cityName}. Anda tidak perlu mencocokkan jam secara manual lagi!",
                        fontSize = 11.sp,
                        color = IvoryWhite,
                        lineHeight = 16.sp
                      )

                      if (uiState.settings.lastOnlineSyncFormatted.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = "🕒 Terakhir disinkronkan: ${uiState.settings.lastOnlineSyncFormatted}",
                          fontSize = 10.sp,
                          color = SoftGray
                        )
                      }

                      if (uiState.onlineSyncMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = uiState.onlineSyncMessage,
                          fontSize = 11.sp,
                          color = Emerald300,
                          fontWeight = FontWeight.Medium
                        )
                      }

                      Spacer(modifier = Modifier.height(10.dp))

                      // Preview Grid of 8 prayer times directly fetched from online Kemenag
                      Text(
                        text = "Jadwal Online Kemenag RI Hari Ini (${uiState.settings.cityName}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                      )
                      Spacer(modifier = Modifier.height(6.dp))

                      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val chunkedPrayers = uiState.prayerTimes.chunked(4)
                        chunkedPrayers.forEach { rowPrayers ->
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            rowPrayers.forEach { prayer ->
                              Box(
                                modifier = Modifier
                                  .weight(1f)
                                  .background(Obsidian950, RoundedCornerShape(8.dp))
                                  .border(1.dp, if (prayer.isUpcoming) Gold400 else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                  .padding(vertical = 6.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                              ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                  Text(
                                    text = prayer.type.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (prayer.isUpcoming) Gold400 else IvoryWhite
                                  )
                                  Text(
                                    text = prayer.timeString,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (prayer.isUpcoming) Gold500 else Emerald300
                                  )
                                }
                              }
                            }
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(10.dp))

                      // Sync now button
                      OutlinedButton(
                        onClick = { onSyncOnlineNow() },
                        enabled = !uiState.isSyncingOnline,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Gold400),
                        colors = ButtonDefaults.outlinedButtonColors(
                          contentColor = Gold400
                        ),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        if (uiState.isSyncingOnline) {
                          CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Gold400,
                            strokeWidth = 2.dp
                          )
                          Spacer(modifier = Modifier.width(8.dp))
                          Text("Menyinkronkan...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                          Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(8.dp))
                          Text("Sinkronkan Ulang Sekarang (Online)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                } else {
                  // Offline explanation
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(Obsidian800, RoundedCornerShape(10.dp))
                      .padding(12.dp)
                  ) {
                    Column {
                      Text(
                        text = "Mode Manual / Hisab Lokal Aktif",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IvoryWhite
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "Jadwal saat ini dihitung secara offline dari rumus sudut posisi matahari. Aktifkan sakelar di atas agar jadwal otomatis mengambil dari server online Kemenag RI.",
                        fontSize = 11.sp,
                        color = SoftGray
                      )
                    }
                  }
                }
              }
            }
          }

          // Card 2: Lokasi & Area Aktif
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .background(Emerald800, CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.MyLocation, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("Area & Titik Koordinat Masjid", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = IvoryWhite)
                      Text("Menentukan titik rujukan data jadwal online Kemenag", fontSize = 11.sp, color = SoftGray)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current Active Area Info Box
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                ) {
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.PinDrop, contentDescription = null, tint = Emerald300, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Area Aktif: ${uiState.settings.cityName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IvoryWhite
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Alamat: ${uiState.settings.mosqueAddress}",
                      fontSize = 11.sp,
                      color = SoftGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Koordinat: ${String.format(java.util.Locale.US, "%.4f, %.4f", uiState.settings.latitude, uiState.settings.longitude)} • Zona: UTC+${uiState.settings.timezoneOffset.toInt()} (${if (uiState.settings.timezoneOffset == 7.0) "WIB" else if (uiState.settings.timezoneOffset == 8.0) "WITA" else "WIT"})",
                      fontSize = 11.sp,
                      fontFamily = FontFamily.Monospace,
                      color = Gold400
                    )
                  }
                }

                if (uiState.locationDetectionMessage.isNotBlank()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = uiState.locationDetectionMessage,
                    fontSize = 11.sp,
                    color = Emerald300,
                    fontWeight = FontWeight.Medium
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                  onClick = { requestAutoLocation() },
                  enabled = !uiState.isDetectingLocation,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detect_location_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                ) {
                  if (uiState.isDetectingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mendeteksi Lokasi & Menarik Jadwal Online...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  } else {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deteksi Lokasi Saya & Tarik Jadwal Online", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }
          }

          // Search manual city
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Pilih Manual Kota / Kabupaten di Indonesia:", fontSize = 13.sp, color = IvoryWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = citySearchQuery,
                  onValueChange = { citySearchQuery = it },
                  placeholder = { Text("Cari kota/kabupaten... (misal: Kediri, Surabaya, Jakarta)", fontSize = 12.sp) },
                  leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp)) },
                  modifier = Modifier.fillMaxWidth(),
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val filteredCities = if (citySearchQuery.isBlank()) {
                  CityData.cities
                } else {
                  CityData.cities.filter {
                    it.name.contains(citySearchQuery, ignoreCase = true) ||
                    it.province.contains(citySearchQuery, ignoreCase = true)
                  }
                }

                if (filteredCities.isEmpty()) {
                  Text("Tidak ada kota yang cocok dengan '$citySearchQuery'", fontSize = 11.sp, color = SoftGray)
                } else {
                  LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(filteredCities) { city ->
                      val isSelected = uiState.settings.cityName.equals(city.name, ignoreCase = true)
                      FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCity(city) },
                        label = { Text("${city.name} (${city.province})") },
                        colors = FilterChipDefaults.filterChipColors(
                          selectedContainerColor = Gold500,
                          selectedLabelColor = Color.Black,
                          containerColor = Obsidian900,
                          labelColor = IvoryWhite
                        )
                      )
                    }
                  }
                }
              }
            }
          }

          item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Koreksi Menit Sholat (Ihtiyat Manual - Opsional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Text(
                  text = if (uiState.settings.useOnlineSchedule) {
                    "💡 Jadwal online Kemenag sudah tepat otomatis. Koreksi ini opsional (disarankan 0 atau +2 menit aman)."
                  } else {
                    "Penyesuaian toleransi menit lokal jika ada selisih kalender cetak"
                  },
                  fontSize = 11.sp,
                  color = if (uiState.settings.useOnlineSchedule) Emerald300 else SoftGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                IhtiyatRow("Subuh", uiState.settings.correctionSubuh) { d -> onUpdateSettings(uiState.settings.copy(correctionSubuh = (uiState.settings.correctionSubuh + d).coerceIn(-10, 10))) }
                IhtiyatRow("Dzuhur", uiState.settings.correctionDzuhur) { d -> onUpdateSettings(uiState.settings.copy(correctionDzuhur = (uiState.settings.correctionDzuhur + d).coerceIn(-10, 10))) }
                IhtiyatRow("Ashar", uiState.settings.correctionAshar) { d -> onUpdateSettings(uiState.settings.copy(correctionAshar = (uiState.settings.correctionAshar + d).coerceIn(-10, 10))) }
                IhtiyatRow("Maghrib", uiState.settings.correctionMaghrib) { d -> onUpdateSettings(uiState.settings.copy(correctionMaghrib = (uiState.settings.correctionMaghrib + d).coerceIn(-10, 10))) }
                IhtiyatRow("Isya", uiState.settings.correctionIsya) { d -> onUpdateSettings(uiState.settings.copy(correctionIsya = (uiState.settings.correctionIsya + d).coerceIn(-10, 10))) }

                if (uiState.settings.useOnlineSchedule && (uiState.settings.correctionSubuh != 0 || uiState.settings.correctionDzuhur != 0 || uiState.settings.correctionAshar != 0 || uiState.settings.correctionMaghrib != 0 || uiState.settings.correctionIsya != 0)) {
                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedButton(
                    onClick = {
                      onUpdateSettings(
                        uiState.settings.copy(
                          correctionSubuh = 0,
                          correctionDzuhur = 0,
                          correctionAshar = 0,
                          correctionMaghrib = 0,
                          correctionIsya = 0,
                          correctionImsak = 0
                        )
                      )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Gold400.copy(alpha = 0.5f))
                  ) {
                    Text("Setel Semua Koreksi ke 0 (Sesuai Online Murni)", fontSize = 11.sp, color = Gold400)
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Tab 2: Iqomah & Alarm
          item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Durasi Hitung Mundur Iqomah (Menit):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Spacer(modifier = Modifier.height(8.dp))
                IhtiyatRow("Iqomah Subuh", uiState.settings.iqomahSubuh, " mnt") { d -> onUpdateSettings(uiState.settings.copy(iqomahSubuh = (uiState.settings.iqomahSubuh + d).coerceIn(1, 30))) }
                IhtiyatRow("Iqomah Dzuhur", uiState.settings.iqomahDzuhur, " mnt") { d -> onUpdateSettings(uiState.settings.copy(iqomahDzuhur = (uiState.settings.iqomahDzuhur + d).coerceIn(1, 30))) }
                IhtiyatRow("Iqomah Ashar", uiState.settings.iqomahAshar, " mnt") { d -> onUpdateSettings(uiState.settings.copy(iqomahAshar = (uiState.settings.iqomahAshar + d).coerceIn(1, 30))) }
                IhtiyatRow("Iqomah Maghrib", uiState.settings.iqomahMaghrib, " mnt") { d -> onUpdateSettings(uiState.settings.copy(iqomahMaghrib = (uiState.settings.iqomahMaghrib + d).coerceIn(1, 30))) }
                IhtiyatRow("Iqomah Isya", uiState.settings.iqomahIsya, " mnt") { d -> onUpdateSettings(uiState.settings.copy(iqomahIsya = (uiState.settings.iqomahIsya + d).coerceIn(1, 30))) }
                IhtiyatRow("Layar Hening Sholat", uiState.settings.sholatBlankMinutes, " mnt") { d -> onUpdateSettings(uiState.settings.copy(sholatBlankMinutes = (uiState.settings.sholatBlankMinutes + d).coerceIn(5, 30))) }
              }
            }
          }

          item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Uji Simulasi Langsung (Test Mode)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                  Button(onClick = { onTestAdzan(PrayerType.MAGHRIB) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)) {
                    Text("Tes Adzan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                  Button(onClick = { onTestIqomah(PrayerType.MAGHRIB, 30) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.Black)) {
                    Text("Tes Iqomah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                  Button(onClick = { onTestSholatMode(2) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Emerald800, contentColor = IvoryWhite)) {
                    Text("Tes Sholat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        3 -> {
          // Tab 3: Running Text
          item {
            var newText by remember { mutableStateOf("") }
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                  value = newText,
                  onValueChange = { newText = it },
                  label = { Text("Tulis pesan pengumuman baru...") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = SoftGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    if (newText.isNotBlank()) {
                      onAddRunningText(newText)
                      newText = ""
                    }
                  },
                  modifier = Modifier.align(Alignment.End),
                  colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Tambahkan", fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          items(uiState.settings.runningTexts.mapIndexed { index, s -> Pair(index, s) }) { (idx, text) ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text, fontSize = 12.sp, color = IvoryWhite, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemoveRunningText(idx) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CrimsonAlert)
                }
              }
            }
          }
        }

        4 -> {
          // Tab 4: Kas, Petugas Jum'at, Hadits & Profil Masjid
          // 1. EDIT LAPORAN KEUANGAN KAS MASJID
          item {
            val rupiahFormatter = remember {
              NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
              }
            }
            var saldoText by remember(uiState.settings.kasSaldo) { mutableStateOf(uiState.settings.kasSaldo.toString()) }
            var masukText by remember(uiState.settings.kasPemasukan) { mutableStateOf(uiState.settings.kasPemasukan.toString()) }
            var keluarText by remember(uiState.settings.kasPengeluaran) { mutableStateOf(uiState.settings.kasPengeluaran.toString()) }

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Gold400.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Laporan Keuangan Kas Masjid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold400)
                    Text("Ditampilkan transparan di carousel info TV", fontSize = 11.sp, color = SoftGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Saldo Kas
                OutlinedTextField(
                  value = saldoText,
                  onValueChange = { saldoText = it.filter { ch -> ch.isDigit() } },
                  label = { Text("Saldo Kas Akhir (Rp)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.fillMaxWidth(),
                  supportingText = {
                    val num = saldoText.toLongOrNull() ?: 0L
                    Text("Pratinjau: ${rupiahFormatter.format(num)}", color = Emerald300, fontSize = 11.sp)
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pemasukan
                OutlinedTextField(
                  value = masukText,
                  onValueChange = { masukText = it.filter { ch -> ch.isDigit() } },
                  label = { Text("Pemasukan Kas Pekan Ini (Rp)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.fillMaxWidth(),
                  supportingText = {
                    val num = masukText.toLongOrNull() ?: 0L
                    Text("Pratinjau: +${rupiahFormatter.format(num)}", color = Emerald500, fontSize = 11.sp)
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pengeluaran
                OutlinedTextField(
                  value = keluarText,
                  onValueChange = { keluarText = it.filter { ch -> ch.isDigit() } },
                  label = { Text("Pengeluaran Kas Pekan Ini (Rp)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.fillMaxWidth(),
                  supportingText = {
                    val num = keluarText.toLongOrNull() ?: 0L
                    Text("Pratinjau: -${rupiahFormatter.format(num)}", color = Color(0xFFF87171), fontSize = 11.sp)
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    val s = saldoText.toLongOrNull() ?: uiState.settings.kasSaldo
                    val m = masukText.toLongOrNull() ?: uiState.settings.kasPemasukan
                    val k = keluarText.toLongOrNull() ?: uiState.settings.kasPengeluaran
                    onUpdateSettings(uiState.settings.copy(kasSaldo = s, kasPemasukan = m, kasPengeluaran = k))
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Simpan Laporan Kas", fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          // 2. EDIT PETUGAS SHOLAT JUM'AT
          item {
            var khotibText by remember(uiState.settings.jumatKhotib) { mutableStateOf(uiState.settings.jumatKhotib) }
            var imamText by remember(uiState.settings.jumatImam) { mutableStateOf(uiState.settings.jumatImam) }
            var muadzinText by remember(uiState.settings.jumatMuadzin) { mutableStateOf(uiState.settings.jumatMuadzin) }

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Jadwal Petugas Sholat Jum'at", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold400)
                    Text("Jadwal Khotib, Imam, & Muadzin Jum'at", fontSize = 11.sp, color = SoftGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                  value = khotibText,
                  onValueChange = { khotibText = it },
                  label = { Text("Nama Khotib Jum'at") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = imamText,
                  onValueChange = { imamText = it },
                  label = { Text("Nama Imam Sholat Jum'at") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = muadzinText,
                  onValueChange = { muadzinText = it },
                  label = { Text("Nama Muadzin Jum'at") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    onUpdateSettings(
                      uiState.settings.copy(
                        jumatKhotib = khotibText.trim(),
                        jumatImam = imamText.trim(),
                        jumatMuadzin = muadzinText.trim()
                      )
                    )
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Simpan Petugas Jum'at", fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          // 3. EDIT MUTIARA HADITS / PESAN HARIAN
          item {
            var haditsText by remember(uiState.settings.mutiaraHadits) { mutableStateOf(uiState.settings.mutiaraHadits) }

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Gold400.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Mutiara Hadits / Pesan Harian", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold400)
                    Text("Slide mutiara hikmah di layar TV", fontSize = 11.sp, color = SoftGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                  value = haditsText,
                  onValueChange = { haditsText = it },
                  label = { Text("Teks Hadits / Pesan Hikmah") },
                  modifier = Modifier.fillMaxWidth(),
                  minLines = 2,
                  maxLines = 4,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    onUpdateSettings(uiState.settings.copy(mutiaraHadits = haditsText.trim()))
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Simpan Pesan Hadits", fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          // 4. EDIT IDENTITAS MASJID
          item {
            var name by remember(uiState.settings.mosqueName) { mutableStateOf(uiState.settings.mosqueName) }
            var address by remember(uiState.settings.mosqueAddress) { mutableStateOf(uiState.settings.mosqueAddress) }

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Identitas & Profil Masjid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold400)
                    Text("Nama dan alamat masjid pada judul layar", fontSize = 11.sp, color = SoftGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                  value = name,
                  onValueChange = { name = it },
                  label = { Text("Nama Masjid") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = address,
                  onValueChange = { address = it },
                  label = { Text("Alamat / Kota Masjid") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                  )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    onUpdateSettings(uiState.settings.copy(mosqueName = name.trim(), mosqueAddress = address.trim()))
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald800, contentColor = IvoryWhite)
                ) {
                  Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Simpan Profil Masjid", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        5 -> {
          // Tab 5: Panel Admin & Lisensi 1 Perangkat
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.9f)),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.5.dp, Gold400)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                      modifier = Modifier.size(42.dp).background(Emerald800, CircleShape).border(1.5.dp, Gold400, CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Devices, contentDescription = null, tint = Gold400, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text("Kebijakan 1 Layar Masjid", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Gold400)
                      Text("Single Device Policy", fontSize = 11.sp, color = IvoryWhite)
                    }
                  }

                  Box(
                    modifier = Modifier
                      .background(Emerald500, RoundedCornerShape(6.dp))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text("AKTIF", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                ) {
                  Column {
                    Text("Perangkat Anda: ${uiState.currentDeviceModel}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                    Text("Hardware ID: ${uiState.currentDeviceId}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SoftGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "🔒 Akun langganan Rp 10.000/bln terikat pada 1 perangkat ini untuk melindungi dari pemakaian di masjid lain. Jika Anda mengganti TV/HP pengurus, Admin dapat mereset kunci perangkat.",
                      fontSize = 11.sp,
                      color = IvoryWhite,
                      lineHeight = 15.sp
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = onOpenAccountDialog,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Gold400),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold400)
                  ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lisensi Layar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = onOpenReportIssue,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800, contentColor = IvoryWhite)
                  ) {
                    Text("Lapor Kendala", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          // Card Bantuan & Kebijakan Privasi
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Bantuan & Kebijakan Aplikasi:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Perlu bantuan reset perangkat, kendala jadwal sholat, atau ingin membaca transparansi penggunaan data lokasi GPS & perangkat?",
                  fontSize = 11.sp,
                  color = SoftGray,
                  lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = onOpenReportIssue,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.7f))
                  ) {
                    Text("Pusat Bantuan", fontSize = 11.sp, color = Emerald300, fontWeight = FontWeight.Bold)
                  }

                  OutlinedButton(
                    onClick = onOpenPrivacyPolicy,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Gold400.copy(alpha = 0.7f))
                  ) {
                    Text("Kebijakan Privasi", fontSize = 11.sp, color = Gold400, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, Gold400.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier.size(42.dp).background(Emerald800, CircleShape).border(1.5.dp, Gold400, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Gold400, modifier = Modifier.size(24.dp))
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text("Panel Super Admin (Pemilik)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Gold400)
                    Text("Monitoring Lokasi Masjid & Pelanggan", fontSize = 11.sp, color = SoftGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "Fitur khusus bagi Anda sebagai pemilik aplikasi untuk:\n• Melihat daftar seluruh masjid di Indonesia yang berlangganan Rp 10.000/bln.\n• Melihat titik koordinat GPS masjid langsung di Google Maps.\n• Mereset ikatan perangkat jika pengurus masjid ganti HP/TV baru.\n• Mengaktifkan/menonaktifkan status langganan secara manual.",
                  fontSize = 11.sp,
                  color = IvoryWhite,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                  onClick = onOpenAdminPanel,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_admin_panel_tab_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Buka Panel Super Admin (Butuh PIN)", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun VerticalPrayerRowItem(prayer: PrayerTimeItem) {
  val isUpcoming = prayer.isUpcoming
  val isPassed = prayer.isPassed

  val borderStroke = if (isUpcoming) {
    BorderStroke(1.5.dp, Gold400)
  } else {
    BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
  }

  val containerColor = if (isUpcoming) {
    Emerald900.copy(alpha = 0.85f)
  } else if (isPassed) {
    Obsidian800.copy(alpha = 0.5f)
  } else {
    Obsidian800
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = borderStroke
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Name & Arabic
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .background(if (isUpcoming) Gold500 else Emerald800, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = prayer.type.displayName.take(1),
            color = if (isUpcoming) Color.Black else IvoryWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = prayer.type.displayName.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUpcoming) Gold400 else IvoryWhite
          )
          Text(
            text = prayer.type.arabicName,
            fontSize = 11.sp,
            color = if (isUpcoming) Emerald300 else SoftGray
          )
        }
      }

      // Right: Time and Status badge
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = prayer.timeString,
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          color = if (isUpcoming) Gold400 else IvoryWhite
        )

        Spacer(modifier = Modifier.width(10.dp))

        if (isUpcoming) {
          Box(
            modifier = Modifier
              .background(Gold500, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text("SELANJUTNYA", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
          }
        } else if (isPassed) {
          Box(
            modifier = Modifier
              .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("LEWAT", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = SoftGray)
          }
        }
      }
    }
  }
}

@Composable
private fun IhtiyatRow(
  label: String,
  currentVal: Int,
  unit: String = " mnt",
  onChange: (Int) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, fontSize = 13.sp, color = IvoryWhite)

    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = { onChange(-1) },
        modifier = Modifier
          .size(28.dp)
          .background(Emerald800, CircleShape)
      ) {
        Text("-", color = Gold400, fontWeight = FontWeight.Bold)
      }

      Text(
        text = "${if (currentVal > 0 && unit == " mnt") "+" else ""}$currentVal$unit",
        color = Gold400,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      IconButton(
        onClick = { onChange(1) },
        modifier = Modifier
          .size(28.dp)
          .background(Emerald800, CircleShape)
      ) {
        Text("+", color = Gold400, fontWeight = FontWeight.Bold)
      }
    }
  }
}
