package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerTimeItem
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.SoftGray

@Composable
fun PrayerTimeCard(
  item: PrayerTimeItem,
  modifier: Modifier = Modifier,
  isCompactHeight: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  val cardBorderColor by animateColorAsState(
    targetValue = if (item.isUpcoming) Gold400 else Color.White.copy(alpha = 0.12f),
    animationSpec = tween(500),
    label = "border_color"
  )

  val backgroundBrush = if (item.isUpcoming) {
    Brush.verticalGradient(
      colors = listOf(
        Emerald800.copy(alpha = 0.95f),
        Color(0xFF0F3A24).copy(alpha = 0.95f),
        Obsidian900.copy(alpha = 0.98f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Obsidian800.copy(alpha = 0.85f),
        Obsidian900.copy(alpha = 0.92f)
      )
    )
  }

  Box(
    modifier = modifier
      .background(
        brush = backgroundBrush,
        shape = RoundedCornerShape(10.dp)
      )
      .border(
        width = if (item.isUpcoming) 2.dp else 1.dp,
        color = if (item.isUpcoming) cardBorderColor.copy(alpha = glowAlpha) else cardBorderColor,
        shape = RoundedCornerShape(10.dp)
      )
      .padding(horizontal = 4.dp, vertical = if (isCompactHeight) 5.dp else 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      // Top line: Arabic name or Upcoming Tag
      if (item.isUpcoming) {
        Box(
          modifier = Modifier
            .background(Gold500, shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = "SELANJUTNYA",
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            maxLines = 1,
            softWrap = false
          )
        }
      } else {
        Text(
          text = item.type.arabicName,
          fontSize = 9.sp,
          color = Gold400.copy(alpha = 0.85f),
          fontWeight = FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Clip
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      // Prayer Name
      Text(
        text = item.type.displayName.uppercase(),
        fontSize = if (isCompactHeight) 11.sp else 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (item.isUpcoming) IvoryWhite else SoftGray,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(if (isCompactHeight) 1.dp else 3.dp))

      // Prayer Time (Always Bold and Visible)
      Text(
        text = item.timeString,
        fontSize = if (isCompactHeight) 16.sp else 19.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        color = if (item.isUpcoming) Gold400 else IvoryWhite,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center
      )
    }
  }
}
