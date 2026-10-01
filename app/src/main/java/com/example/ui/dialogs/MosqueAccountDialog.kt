package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MosqueSettings
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray

@Composable
fun MosqueAccountDialog(
  settings: MosqueSettings,
  isProSubscribed: Boolean,
  deviceId: String,
  deviceModel: String,
  onOpenSubscription: () -> Unit,
  onOpenPrivacyPolicy: () -> Unit = {},
  onOpenReportIssue: () -> Unit = {},
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier.fillMaxWidth(0.95f),
      shape = RoundedCornerShape(20.dp),
      color = Obsidian950,
      border = BorderStroke(1.5.dp, Gold400.copy(alpha = 0.8f))
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier.size(40.dp).background(Emerald800, CircleShape).border(1.5.dp, Gold400, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Security, contentDescription = null, tint = Gold400, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Lisensi & Perangkat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gold400)
              Text("Kebijakan 1 Layar Masjid", fontSize = 11.sp, color = IvoryWhite)
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mosque Info Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Obsidian800),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Mosque, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(settings.mosqueName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Wilayah: ${settings.cityName} • ${settings.mosqueAddress}", fontSize = 11.sp, color = SoftGray)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Single Device Binding Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Devices, contentDescription = null, tint = Emerald300, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Perangkat Terikat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
              }
              Box(
                modifier = Modifier
                  .background(Emerald500, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("KUNCI AKTIF", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Model: $deviceModel", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IvoryWhite)
            Text("Device ID: $deviceId", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SoftGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "🛡️ Lisensi langganan Rp 10.000/bulan ini dikunci secara otomatis ke perangkat ini agar tidak disalahgunakan di masjid lain. 1 langganan = 1 layar masjid.",
              fontSize = 11.sp,
              color = IvoryWhite,
              lineHeight = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subscription Status
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Obsidian800),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Status Langganan Pro", fontSize = 12.sp, color = SoftGray)
              Text(
                text = if (isProSubscribed) "PRO AKTIF (Rp 10.000 / Bulan)" else "Versi Standar",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isProSubscribed) Emerald300 else Gold400
              )
            }

            if (!isProSubscribed) {
              Button(
                onClick = {
                  onDismiss()
                  onOpenSubscription()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
              ) {
                Text("Langganan", fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            } else {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500, modifier = Modifier.size(24.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Helpdesk & Privacy links
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              onDismiss()
              onOpenReportIssue()
            },
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Lapor Kendala", fontSize = 11.sp, color = Emerald300, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              onDismiss()
              onOpenPrivacyPolicy()
            },
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Gold400.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Kebijakan Privasi", fontSize = 11.sp, color = Gold400, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Obsidian800, contentColor = IvoryWhite)
        ) {
          Text("Tutup")
        }
      }
    }
  }
}
