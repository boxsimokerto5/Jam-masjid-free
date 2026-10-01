package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MosqueSettings
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.SoftGray
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MosqueInfoCarousel(
  settings: MosqueSettings,
  activeSlideIndex: Int,
  modifier: Modifier = Modifier,
  isCompact: Boolean = false
) {
  val idFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
    maximumFractionDigits = 0
  }

  Box(
    modifier = modifier
      .background(
        color = Obsidian900.copy(alpha = 0.75f),
        shape = RoundedCornerShape(14.dp)
      )
      .border(
        width = 1.dp,
        color = Gold500.copy(alpha = 0.35f),
        shape = RoundedCornerShape(14.dp)
      )
      .padding(horizontal = if (isCompact) 12.dp else 14.dp, vertical = if (isCompact) 6.dp else 12.dp)
  ) {
    AnimatedContent(
      targetState = activeSlideIndex,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "info_slide_carousel"
    ) { slide ->
      when (slide) {
        0 -> {
          // Slide 0: Laporan Kas Keuangan Masjid
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "LAPORAN KAS MASJID",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Gold400,
                modifier = Modifier.padding(start = 6.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Saldo Akhir", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = idFormat.format(settings.kasSaldo),
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = Emerald300
                )
              }

              Column {
                Text(text = "Pemasukan", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = "+ " + idFormat.format(settings.kasPemasukan),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Emerald500
                )
              }

              Column {
                Text(text = "Pengeluaran", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = "- " + idFormat.format(settings.kasPengeluaran),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFFF87171)
                )
              }
            }
          }
        }

        1 -> {
          // Slide 1: Petugas Sholat Jum'at
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "PETUGAS SHOLAT JUM'AT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Gold400,
                modifier = Modifier.padding(start = 6.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Khotib:", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatKhotib,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Imam:", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatImam,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Muadzin:", fontSize = 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatMuadzin,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1
                )
              }
            }
          }
        }

        else -> {
          // Slide 2: Mutiara Hadits
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "MUTIARA SUNNAH",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Gold400,
                modifier = Modifier.padding(start = 6.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = settings.mutiaraHadits,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = IvoryWhite,
              lineHeight = 18.sp
            )
          }
        }
      }
    }
  }
}
