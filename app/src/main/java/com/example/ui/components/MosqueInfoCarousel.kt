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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        brush = Brush.verticalGradient(
          colors = listOf(
            Obsidian900.copy(alpha = 0.88f),
            Color(0xFF091410).copy(alpha = 0.94f)
          )
        ),
        shape = RoundedCornerShape(14.dp)
      )
      .border(
        width = 1.dp,
        color = Gold500.copy(alpha = 0.45f),
        shape = RoundedCornerShape(14.dp)
      )
      .padding(horizontal = if (isCompact) 10.dp else 14.dp, vertical = if (isCompact) 6.dp else 10.dp)
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
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AccountBalanceWallet,
                  contentDescription = null,
                  tint = Gold400,
                  modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                )
                Text(
                  text = "LAPORAN KAS & SALDO KEUANGAN",
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400,
                  modifier = Modifier.padding(start = 6.dp)
                )
              }

              // Slide Dots Indicator
              SlideDotsIndicator(currentIndex = 0)
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Saldo Akhir
              Column {
                Text(
                  text = "Saldo Akhir Kas",
                  fontSize = if (isCompact) 10.sp else 11.5.sp,
                  color = SoftGray
                )
                Text(
                  text = idFormat.format(settings.kasSaldo),
                  fontSize = if (isCompact) 15.sp else 19.sp,
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace,
                  color = Emerald300
                )
              }

              // Pemasukan
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Pemasukan",
                  fontSize = if (isCompact) 9.5.sp else 11.sp,
                  color = SoftGray
                )
                Text(
                  text = "+ " + idFormat.format(settings.kasPemasukan),
                  fontSize = if (isCompact) 11.5.sp else 13.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Emerald500
                )
              }

              // Pengeluaran
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Pengeluaran",
                  fontSize = if (isCompact) 9.5.sp else 11.sp,
                  color = SoftGray
                )
                Text(
                  text = "- " + idFormat.format(settings.kasPengeluaran),
                  fontSize = if (isCompact) 11.5.sp else 13.5.sp,
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
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = Gold400,
                  modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                )
                Text(
                  text = "JADWAL PETUGAS JUM'AT",
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400,
                  modifier = Modifier.padding(start = 6.dp)
                )
              }

              SlideDotsIndicator(currentIndex = 1)
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Khotib:", fontSize = if (isCompact) 9.5.sp else 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatKhotib,
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Imam:", fontSize = if (isCompact) 9.5.sp else 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatImam,
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Muadzin:", fontSize = if (isCompact) 9.5.sp else 11.sp, color = SoftGray)
                Text(
                  text = settings.jumatMuadzin,
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = IvoryWhite,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }

        else -> {
          // Slide 2: Kata Hikmah & Mutiara Hadits
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Gold400,
                  modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                )
                Text(
                  text = "KATA HIKMAH & MUTIARA HADITS",
                  fontSize = if (isCompact) 11.5.sp else 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400,
                  modifier = Modifier.padding(start = 6.dp)
                )
              }

              SlideDotsIndicator(currentIndex = 2)
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 6.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = Gold500.copy(alpha = 0.7f),
                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = settings.mutiaraHadits,
                fontSize = if (isCompact) 11.5.sp else 13.5.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                color = IvoryWhite,
                lineHeight = if (isCompact) 16.sp else 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SlideDotsIndicator(currentIndex: Int) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    repeat(3) { idx ->
      Box(
        modifier = Modifier
          .size(if (idx == currentIndex) 7.dp else 5.dp)
          .background(
            color = if (idx == currentIndex) Gold400 else Color.White.copy(alpha = 0.25f),
            shape = CircleShape
          )
      )
    }
  }
}
