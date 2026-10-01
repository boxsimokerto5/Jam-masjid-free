package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MosqueSubscriber
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MosqueAdminPanelDialog(
  subscribers: List<MosqueSubscriber>,
  isLoading: Boolean = false,
  onVerifyPin: (String) -> Boolean,
  onResetDeviceBinding: (String) -> Unit,
  onTogglePro: (String, Boolean) -> Unit,
  onRefreshData: () -> Unit,
  onChangePin: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var isAuthenticated by remember { mutableStateOf(false) }
  var pinInput by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var showPinSettings by remember { mutableStateOf(false) }
  var newPinInput by remember { mutableStateOf("") }
  var pinChangeSuccess by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f),
      shape = RoundedCornerShape(20.dp),
      color = Obsidian950,
      border = BorderStroke(1.5.dp, Gold400.copy(alpha = 0.8f))
    ) {
      if (!isAuthenticated) {
        // PIN VERIFICATION SCREEN
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.85f)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .background(Emerald800, CircleShape)
                .border(2.dp, Gold400, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(36.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Panel Super Admin",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Gold400
            )
            Text(
              text = "Monitoring Lokasi & Langganan Masjid (Rp 10.000/bln)",
              fontSize = 12.sp,
              color = SoftGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
              value = pinInput,
              onValueChange = {
                if (it.length <= 6) {
                  pinInput = it
                  pinError = false
                }
              },
              label = { Text("Masukkan PIN Keamanan Admin (6 Digit)") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              visualTransformation = PasswordVisualTransformation(),
              isError = pinError,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = SoftGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              )
            )

            if (pinError) {
              Spacer(modifier = Modifier.height(6.dp))
              Text("PIN salah! Coba lagi (Default: 192837)", color = CrimsonAlert, fontSize = 11.sp)
            } else {
              Spacer(modifier = Modifier.height(6.dp))
              Text("PIN default bawaan: 192837", color = SoftGray, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, SoftGray)
              ) {
                Text("Batal", color = SoftGray)
              }

              Button(
                onClick = {
                  if (onVerifyPin(pinInput)) {
                    isAuthenticated = true
                    pinError = false
                  } else {
                    pinError = true
                  }
                },
                modifier = Modifier.weight(1.5f),
                colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
              ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Buka Panel", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        // AUTHENTICATED ADMIN DASHBOARD
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
          // Header Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier.size(36.dp).background(Emerald800, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("PANEL MONITORING ADMIN", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Gold400)
                Text("Database Pelanggan Masjid & Single Device Policy", fontSize = 11.sp, color = Emerald300)
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(onClick = onRefreshData) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Gold400)
              }
              IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Statistics Overview Cards
          val totalMasjid = subscribers.size
          val proActiveCount = subscribers.count { it.isPro }
          val totalRevenueEst = proActiveCount * 10000L
          val rupiahFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
          }.format(totalRevenueEst)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AdminStatCard(
              title = "Total Masjid",
              value = "$totalMasjid",
              sub = "Terdaftar",
              icon = Icons.Default.Mosque,
              tint = Gold400,
              modifier = Modifier.weight(1f)
            )
            AdminStatCard(
              title = "Langganan Aktif",
              value = "$proActiveCount",
              sub = "Rp 10.000/bln",
              icon = Icons.Default.CheckCircle,
              tint = Emerald500,
              modifier = Modifier.weight(1f)
            )
            AdminStatCard(
              title = "Omzet Bulanan",
              value = rupiahFmt,
              sub = "Estimasi Bruto",
              icon = Icons.Default.AttachMoney,
              tint = Gold500,
              modifier = Modifier.weight(1.3f)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Search and Filter Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Cari nama masjid atau kota...", fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp)) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              )
            )

            Button(
              onClick = { showPinSettings = !showPinSettings },
              colors = ButtonDefaults.buttonColors(containerColor = Obsidian800, contentColor = Gold400),
              border = BorderStroke(1.dp, Gold400.copy(alpha = 0.5f))
            ) {
              Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Optional Change PIN Section
          AnimatedVisibility(visible = showPinSettings) {
            Card(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              colors = CardDefaults.cardColors(containerColor = Emerald900),
              shape = RoundedCornerShape(10.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Ganti PIN Keamanan Admin:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gold400)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { if (it.length <= 6) newPinInput = it },
                    placeholder = { Text("PIN baru (6 digit)", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold400, focusedTextColor = IvoryWhite)
                  )
                  Button(
                    onClick = {
                      if (newPinInput.length >= 4) {
                        onChangePin(newPinInput)
                        pinChangeSuccess = true
                        showPinSettings = false
                        newPinInput = ""
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
                  ) {
                    Text("Simpan PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Mosque List
          val filteredList = subscribers.filter {
            it.mosqueName.contains(searchQuery, ignoreCase = true) ||
            it.cityName.contains(searchQuery, ignoreCase = true) ||
            it.mosqueAddress.contains(searchQuery, ignoreCase = true)
          }

          if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
              CircularProgressIndicator(color = Gold400)
            }
          } else if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
              Text("Belum ada data masjid yang cocok.", color = SoftGray, fontSize = 13.sp)
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxWidth().weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredList) { mosque ->
                MosqueSubscriberItemCard(
                  mosque = mosque,
                  onOpenGoogleMaps = {
                    val uri = Uri.parse("geo:${mosque.latitude},${mosque.longitude}?q=${mosque.latitude},${mosque.longitude}(${Uri.encode(mosque.mosqueName)})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                      setPackage("com.google.android.apps.maps")
                    }
                    try {
                      context.startActivity(mapIntent)
                    } catch (_: Exception) {
                      context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                  },
                  onResetDevice = { onResetDeviceBinding(mosque.id) },
                  onTogglePro = { isPro -> onTogglePro(mosque.id, isPro) }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminStatCard(
  title: String,
  value: String,
  sub: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Obsidian800),
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, fontSize = 11.sp, color = SoftGray, maxLines = 1)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = IvoryWhite, maxLines = 1)
      Text(sub, fontSize = 10.sp, color = tint, maxLines = 1)
    }
  }
}

@Composable
private fun MosqueSubscriberItemCard(
  mosque: MosqueSubscriber,
  onOpenGoogleMaps: () -> Unit,
  onResetDevice: () -> Unit,
  onTogglePro: (Boolean) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Obsidian800),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, if (mosque.isPro) Emerald500.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.1f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header: Mosque Name & Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(Icons.Default.Mosque, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(mosque.mosqueName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            Text("Kota: ${mosque.cityName}", fontSize = 11.sp, color = Emerald300)
          }
        }

        Box(
          modifier = Modifier
            .background(if (mosque.isPro) Emerald500 else Color.Gray, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = if (mosque.isPro) "PRO AKTIF" else "NONAKTIF",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Address & Location Info
      Text(
        text = "📍 ${mosque.mosqueAddress}",
        fontSize = 11.sp,
        color = SoftGray,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Coordinates with Map Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Koordinat: ${String.format(Locale.US, "%.4f, %.4f", mosque.latitude, mosque.longitude)}",
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          color = Gold400
        )

        OutlinedButton(
          onClick = onOpenGoogleMaps,
          border = BorderStroke(0.8.dp, Gold400),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Map, contentDescription = null, tint = Gold400, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Buka Peta", fontSize = 10.sp, color = Gold400, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bound Device Box (Single Device Policy)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Devices, contentDescription = null, tint = Emerald300, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Perangkat Terikat: ${mosque.deviceModel.ifBlank { "Belum Terikat" }}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = IvoryWhite
            )
          }
          if (mosque.activeDeviceId.isNotBlank()) {
            Text(
              text = "ID: ${mosque.activeDeviceId}",
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              color = SoftGray
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Subscription Dates
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Mulai: ${mosque.registeredDate}", fontSize = 10.sp, color = SoftGray)
        Text("Berlaku s/d: ${mosque.expiryDate}", fontSize = 10.sp, color = if (mosque.isPro) Emerald300 else CrimsonAlert)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Admin Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Reset Device Binding
        OutlinedButton(
          onClick = onResetDevice,
          modifier = Modifier.weight(1f),
          border = BorderStroke(1.dp, Gold400.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
        ) {
          Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Gold400, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reset Kunci HP/TV", fontSize = 10.sp, color = Gold400, fontWeight = FontWeight.Bold)
        }

        // Toggle Pro status
        Button(
          onClick = { onTogglePro(!mosque.isPro) },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (mosque.isPro) Obsidian900 else Emerald500,
            contentColor = if (mosque.isPro) CrimsonAlert else Color.Black
          ),
          border = if (mosque.isPro) BorderStroke(1.dp, CrimsonAlert) else null,
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
        ) {
          Text(
            text = if (mosque.isPro) "Cabut Pro" else "Aktifkan Pro",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
