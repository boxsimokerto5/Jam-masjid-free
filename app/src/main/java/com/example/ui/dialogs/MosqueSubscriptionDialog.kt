package com.example.ui.dialogs

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun MosqueSubscriptionDialog(
  isSubscribed: Boolean,
  onDismiss: () -> Unit,
  onSubscribeClicked: (Activity) -> Unit,
  onSimulateUnlock: () -> Unit,
  onRestorePurchases: () -> Unit
) {
  val context = LocalContext.current
  val activity = context as? Activity

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      colors = CardDefaults.cardColors(containerColor = Obsidian900),
      shape = RoundedCornerShape(24.dp),
      border = BorderStroke(2.dp, Gold500)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_subscription_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
          }
        }

        // Hero Badge & Logo
        Box(
          modifier = Modifier
            .size(68.dp)
            .background(
              brush = Brush.radialGradient(listOf(Gold400, Emerald900)),
              shape = CircleShape
            )
            .border(2.dp, Gold500, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = IvoryWhite,
            modifier = Modifier.size(38.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "UPGRADE JAM MASJID PRO",
          fontSize = 19.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Gold400,
          textAlign = TextAlign.Center,
          letterSpacing = 1.sp
        )

        Text(
          text = "Pancarkan ke Layar TV & Buka Semua Fitur Operasional Masjid",
          fontSize = 12.sp,
          color = SoftGray,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Price Tag Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.9f)),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.5.dp, Gold500)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "PAKET LANGGANAN RESMI",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald300
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "Rp 10.000",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = IvoryWhite
              )
              Text(
                text = " / bulan",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gold400,
                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
              )
            }
            Text(
              text = "Setara Rp 333 / hari • Dapat dibatalkan sewaktu-waktu",
              fontSize = 10.sp,
              color = Emerald300
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Features Checklist
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Obsidian800, RoundedCornerShape(14.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ProFeatureItem("Buka Layar TV Tanpa Batas (Miracast & PWA)")
          ProFeatureItem("PWA Offline Cache (HP bebas dibawa pulang)")
          ProFeatureItem("Ganti 5 Pilihan Tema Tata Letak TV Masjid")
          ProFeatureItem("Ubah Nama Masjid, Alamat, & Logo Kustom")
          ProFeatureItem("Modul Laporan Kas Masjid (Infaq & Saldo)")
          ProFeatureItem("Jadwal Petugas Sholat Jum'at & Khotib")
          ProFeatureItem("Teks Berjalan Pengumuman (Running Text)")
          ProFeatureItem("Bebas Iklan & Dukungan Update Prioritas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Play Subscribe Button
        Button(
          onClick = {
            if (activity != null) {
              onSubscribeClicked(activity)
            } else {
              Toast.makeText(context, "Membuka Google Play...", Toast.LENGTH_SHORT).show()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("subscribe_google_play_button"),
          colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Langganan via Google Play - Rp 10.000",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Development / Testing Mode Quick Unlock (For testing before publishing product on Play Console)
        OutlinedButton(
          onClick = onSimulateUnlock,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("simulate_unlock_pro_button"),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.7f))
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = Emerald300, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isSubscribed) "Kunci Kembali (Kembali ke Gratis)" else "Buka Kunci Pro (Uji Coba Pengurus / Admin)",
            color = Emerald300,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Restore Purchases button
        TextButton(
          onClick = onRestorePurchases,
          modifier = Modifier.testTag("restore_purchases_button")
        ) {
          Text(
            text = "Sudah pernah bayar? Pulihkan Pembelian",
            fontSize = 11.sp,
            color = SoftGray
          )
        }
      }
    }
  }
}

@Composable
private fun ProFeatureItem(text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = Gold400,
      modifier = Modifier.size(17.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      color = IvoryWhite
    )
  }
}
