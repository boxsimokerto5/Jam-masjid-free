package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  onSetBackgroundType: (String) -> Unit,
  onSetCustomBackgroundUri: (String) -> Unit,
  onSetOverlayDarkness: (Float) -> Unit,
  onAddRunningText: (String) -> Unit,
  onRemoveRunningText: (Int) -> Unit,
  onTestAdzan: (PrayerType) -> Unit,
  onTestIqomah: (PrayerType, Int) -> Unit,
  onTestSholatMode: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabTitles = listOf("🎨 Tampilan", "🕌 Jadwal", "⏱️ Iqomah", "📜 Running Text", "💰 Kas & Info")

  // PhotoPicker for custom background
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onSetCustomBackgroundUri(uri.toString())
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

        Spacer(modifier = Modifier.width(8.dp))

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
          // Tab 0: Tampilan & Background
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
          // Tab 1: Jadwal & Lokasi
          item {
            Text("Pilihan Kota Cepat:", fontSize = 13.sp, color = IvoryWhite, fontWeight = FontWeight.Bold)
          }

          item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
              items(CityData.cities) { city ->
                val isSelected = uiState.settings.cityName.equals(city.name, ignoreCase = true)
                FilterChip(
                  selected = isSelected,
                  onClick = { onSelectCity(city) },
                  label = { Text(city.name) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Gold500,
                    selectedLabelColor = Color.Black,
                    containerColor = Obsidian800,
                    labelColor = IvoryWhite
                  )
                )
              }
            }
          }

          item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Koreksi Menit Sholat (Ihtiyat)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Spacer(modifier = Modifier.height(8.dp))
                IhtiyatRow("Subuh", uiState.settings.correctionSubuh) { d -> onUpdateSettings(uiState.settings.copy(correctionSubuh = (uiState.settings.correctionSubuh + d).coerceIn(-10, 10))) }
                IhtiyatRow("Dzuhur", uiState.settings.correctionDzuhur) { d -> onUpdateSettings(uiState.settings.copy(correctionDzuhur = (uiState.settings.correctionDzuhur + d).coerceIn(-10, 10))) }
                IhtiyatRow("Ashar", uiState.settings.correctionAshar) { d -> onUpdateSettings(uiState.settings.copy(correctionAshar = (uiState.settings.correctionAshar + d).coerceIn(-10, 10))) }
                IhtiyatRow("Maghrib", uiState.settings.correctionMaghrib) { d -> onUpdateSettings(uiState.settings.copy(correctionMaghrib = (uiState.settings.correctionMaghrib + d).coerceIn(-10, 10))) }
                IhtiyatRow("Isya", uiState.settings.correctionIsya) { d -> onUpdateSettings(uiState.settings.copy(correctionIsya = (uiState.settings.correctionIsya + d).coerceIn(-10, 10))) }
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

        else -> {
          // Tab 4: Kas & Info Masjid
          item {
            var name by remember { mutableStateOf(uiState.settings.mosqueName) }
            var address by remember { mutableStateOf(uiState.settings.mosqueAddress) }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Obsidian800), shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Nama & Alamat Masjid", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = name,
                  onValueChange = { name = it; onUpdateSettings(uiState.settings.copy(mosqueName = it)) },
                  label = { Text("Nama Masjid") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold400, unfocusedBorderColor = SoftGray, focusedTextColor = IvoryWhite, unfocusedTextColor = IvoryWhite)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = address,
                  onValueChange = { address = it; onUpdateSettings(uiState.settings.copy(mosqueAddress = it)) },
                  label = { Text("Alamat / Kota") },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold400, unfocusedBorderColor = SoftGray, focusedTextColor = IvoryWhite, unfocusedTextColor = IvoryWhite)
                )
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
