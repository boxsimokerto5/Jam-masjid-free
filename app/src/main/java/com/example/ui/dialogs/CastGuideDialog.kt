package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

@Composable
fun CastGuideDialog(
  pwaServerUrl: String,
  isPwaServerRunning: Boolean,
  onDismiss: () -> Unit,
  onForceTvMode: () -> Unit
) {
  val context = LocalContext.current
  var isCopied by remember { mutableStateOf(false) }

  fun openCastSettings(ctx: Context) {
    try {
      val intent = Intent(Settings.ACTION_CAST_SETTINGS)
      ctx.startActivity(intent)
    } catch (_: Exception) {
      try {
        val wirelessIntent = Intent("android.settings.WIFI_DISPLAY_SETTINGS")
        ctx.startActivity(wirelessIntent)
      } catch (_: Exception) {
        try {
          ctx.startActivity(Intent(Settings.ACTION_SETTINGS))
          Toast.makeText(ctx, "Pilih opsi 'Koneksi & Berbagi' lalu 'Transmisikan Layar'", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
          Toast.makeText(ctx, "Tidak dapat membuka pengaturan Cast otomatis", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  fun copyToClipboard(text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Mosque TV URL", text)
    clipboard.setPrimaryClip(clip)
    isCopied = true
    Toast.makeText(context, "Alamat disalin: $text", Toast.LENGTH_SHORT).show()
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
      colors = CardDefaults.cardColors(containerColor = Obsidian900),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.5.dp, Gold500.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(Emerald800, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "Hubungkan ke TV Masjid",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IvoryWhite
              )
              Text(
                text = "Pilih cara tayang ke Smart TV",
                fontSize = 12.sp,
                color = SoftGray
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // METODE 1: OFFLINE PWA (HP BISA DIBAWA PULANG)
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.85f)),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.5.dp, Gold400)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CloudDone, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("METODE 1: OFFLINE PWA CACHE", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Gold400)
            }
            Text(
              text = "⭐ HP BEBAS DIBAWA PULANG! TV tetap berjalan mandiri tanpa henti.",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = IvoryWhite,
              modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("1. Buka browser di Smart TV masjid (Chrome, Silk, dsb).", fontSize = 11.sp, color = IvoryWhite)
            Text("2. Masukkan alamat IP lokal berikut di browser TV:", fontSize = 11.sp, color = IvoryWhite)

            Spacer(modifier = Modifier.height(6.dp))

            // URL Box
            val displayUrl = if (pwaServerUrl.isNotBlank()) pwaServerUrl else "http://192.168.1.x:8080"
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .border(1.dp, Emerald500.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .clickable { copyToClipboard(displayUrl) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Language, contentDescription = null, tint = Emerald300, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = displayUrl,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Emerald300
                  )
                }
                Icon(
                  imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = "Salin",
                  tint = Gold400,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "✓ Otomatis tercache di memori TV. TV menghitung jam, waktu sholat, dan adzan sendiri walaupun HP dimatikan atau dibawa keluar masjid.",
              fontSize = 10.sp,
              color = Emerald300,
              lineHeight = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // METODE 2: MIRACAST LANGSUNG
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Obsidian800),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Cast, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("METODE 2: MIRACAST / SMART VIEW", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gold400)
            }
            Text(
              text = "Pancarkan layar HP langsung ke TV (HP harus berada di dekat TV).",
              fontSize = 11.sp,
              color = SoftGray,
              modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = { openCastSettings(context) },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
            ) {
              Icon(Icons.Default.ScreenShare, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Cari & Sambungkan ke TV (Cast)", fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // METODE 3: MODE TV HORIZONTAL DI HP
        OutlinedButton(
          onClick = {
            onForceTvMode()
            onDismiss()
          },
          modifier = Modifier.fillMaxWidth(),
          border = BorderStroke(1.dp, Emerald500)
        ) {
          Icon(Icons.Default.StayCurrentLandscape, contentDescription = null, tint = Emerald500, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Ubah Tampilan ke Mode TV (Horizontal)", color = Emerald500, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
