package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray

@Composable
fun PrivacyPolicyDialog(
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f),
      shape = RoundedCornerShape(20.dp),
      color = Obsidian950,
      border = BorderStroke(1.5.dp, Gold400.copy(alpha = 0.8f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
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
                imageVector = Icons.Default.Policy,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Kebijakan Privasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gold400
              )
              Text(
                text = "Privacy Policy & Perlindungan Data",
                fontSize = 11.sp,
                color = Emerald300
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable content
        val scrollState = rememberScrollState()
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(scrollState),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          PrivacySectionCard(
            title = "1. Pendahuluan & Komitmen Privasi",
            content = "Aplikasi Jam Masjid Digital berkomitmen melindungi privasi pengurus masjid, jamaah, dan pengguna aplikasi. Kebijakan ini menjelaskan transparansi jenis data yang kami proses dan tujuan penggunaannya sesuai regulasi Google Play Developer Policy."
          )

          PrivacySectionCard(
            title = "2. Izin Akses Lokasi (GPS & Jaringan)",
            content = "Aplikasi memerlukan izin lokasi perangkat (ACCESS_FINE_LOCATION dan ACCESS_COARSE_LOCATION) HANYA untuk satu tujuan: menghitung dan mencocokkan jadwal waktu sholat resmi (Kemenag RI) berdasarkan posisi geografis masjid secara akurat. Data lokasi tidak pernah dipantau secara latar belakang di luar keperluan jadwal sholat dan tidak pernah dibagikan kepada pihak ketiga untuk kepentingan iklan."
          )

          PrivacySectionCard(
            title = "3. Identitas Perangkat & Kebijakan 1 Layar Masjid",
            content = "Untuk mencegah pembajakan dan penyalahgunaan lisensi langganan (Rp 10.000/bulan), aplikasi membaca pengenal perangkat keras (Hardware Device ID dan Model Perangkat). Data ini murni digunakan untuk mengunci hak akses Pro pada 1 layar fisik masjid. Pengurus masjid dapat mengajukan permohonan reset kunci perangkat jika mengganti TV/HP baru melalui menu Lapor Kendala."
          )

          PrivacySectionCard(
            title = "4. Pembayaran & Langganan Google Play Billing",
            content = "Seluruh transaksi langganan bulanan diproses secara aman oleh Google Play Billing. Kami tidak pernah menyimpan nomor kartu kredit, nomor rekening, atau informasi finansial sensitif Anda di server kami. Anda dapat membatalkan langganan kapan saja melalui aplikasi Google Play Store > Akun > Pembayaran & Langganan."
          )

          PrivacySectionCard(
            title = "5. Data Masjid & Laporan Keuangan Kas",
            content = "Data nama masjid, teks berjalan, petugas jum'at, mutiara hadits, dan pembukuan kas masjid disimpan secara aman di perangkat lokal dan database cloud tersinkronisasi untuk keperluan operasional jam masjid Anda. Kami tidak menjual atau menyewakan data profil masjid Anda."
          )

          PrivacySectionCard(
            title = "6. Hubungi Pengembang (Bantuan & Hak Pengguna)",
            content = "Jika Anda memiliki pertanyaan seputar kebijakan privasi, permohonan penghapusan data, atau kendala teknis, silakan gunakan menu 'Lapor Kendala' di aplikasi atau hubungi kami melalui kontak resmi pengembang Jam Masjid Digital."
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Saya Mengerti & Setuju", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun PrivacySectionCard(
  title: String,
  content: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Obsidian800),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = IvoryWhite
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = content,
        fontSize = 11.sp,
        color = SoftGray,
        lineHeight = 16.sp
      )
    }
  }
}
