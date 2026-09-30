package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.SoftGray

@Composable
fun CastGuideDialog(
  onDismiss: () -> Unit,
  onForceTvMode: () -> Unit
) {
  val context = LocalContext.current

  fun openCastSettings(ctx: Context) {
    try {
      val intent = Intent(Settings.ACTION_CAST_SETTINGS)
      ctx.startActivity(intent)
    } catch (e: Exception) {
      try {
        val wirelessIntent = Intent("android.settings.WIFI_DISPLAY_SETTINGS")
        ctx.startActivity(wirelessIntent)
      } catch (e2: Exception) {
        try {
          ctx.startActivity(Intent(Settings.ACTION_SETTINGS))
          Toast.makeText(ctx, "Pilih opsi 'Koneksi & Berbagi' lalu 'Transmisikan Layar'", Toast.LENGTH_LONG).show()
        } catch (e3: Exception) {
          Toast.makeText(ctx, "Tidak dapat membuka pengaturan Cast otomatis", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      colors = CardDefaults.cardColors(containerColor = Obsidian900),
      shape = RoundedCornerShape(20.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold500.copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
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
                imageVector = Icons.Default.Cast,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "Koneksi Miracast / TV",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IvoryWhite
              )
              Text(
                text = "Tampilkan Jam di Layar TV Masjid",
                fontSize = 12.sp,
                color = SoftGray
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Steps
        CastStepItem(
          icon = Icons.Default.Wifi,
          stepNum = "1",
          title = "Sambungkan ke Wi-Fi / Hotspot yang sama",
          desc = "Pastikan HP dan Smart TV / Android Box terhubung ke WiFi masjid atau Miracast aktif di TV."
        )

        Spacer(modifier = Modifier.height(10.dp))

        CastStepItem(
          icon = Icons.Default.ScreenShare,
          stepNum = "2",
          title = "Buka Fitur Cast Layar HP",
          desc = "Gunakan tombol di bawah untuk membuka menu Cast / Smart View / Layar Nirkabel di HP."
        )

        Spacer(modifier = Modifier.height(10.dp))

        CastStepItem(
          icon = Icons.Default.Tv,
          stepNum = "3",
          title = "Pilih Perangkat TV Masjid",
          desc = "Pilih nama TV Anda. Begitu tersambung, putar HP ke horizontal atau aktifkan Mode TV!"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
          onClick = {
            openCastSettings(context)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("open_cast_settings_action"),
          colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
        ) {
          Icon(Icons.Default.Cast, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Buka Pengaturan Cast di HP", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = {
            onForceTvMode()
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("toggle_tv_mode_direct"),
          border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500)
        ) {
          Icon(Icons.Default.StayCurrentLandscape, contentDescription = null, tint = Emerald500, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Ubah Tampilan ke Mode TV (Landscape)", color = Emerald500, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
private fun CastStepItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  stepNum: String,
  title: String,
  desc: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Obsidian800, RoundedCornerShape(12.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .background(Emerald800, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stepNum,
        color = Gold400,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column {
      Text(
        text = title,
        color = IvoryWhite,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = desc,
        color = SoftGray,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        modifier = Modifier.padding(top = 2.dp)
      )
    }
  }
}
