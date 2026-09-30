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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "card_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val cardBorderColor by animateColorAsState(
    targetValue = if (item.isUpcoming) Gold400 else Color.White.copy(alpha = 0.15f),
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
        Obsidian800.copy(alpha = 0.8f),
        Obsidian900.copy(alpha = 0.9f)
      )
    )
  }

  Box(
    modifier = modifier
      .then(if (item.isUpcoming) Modifier.scale(pulseScale) else Modifier)
      .background(
        brush = backgroundBrush,
        shape = RoundedCornerShape(12.dp)
      )
      .border(
        width = if (item.isUpcoming) 2.dp else 1.dp,
        color = cardBorderColor,
        shape = RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 8.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Upcoming Tag
      if (item.isUpcoming) {
        Box(
          modifier = Modifier
            .background(Gold500, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "AKAN DATANG",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
      } else {
        // Arabic subtitle
        Text(
          text = item.type.arabicName,
          fontSize = 11.sp,
          color = Gold400.copy(alpha = 0.85f),
          fontWeight = FontWeight.Normal
        )
      }

      // Prayer Name
      Text(
        text = item.type.displayName.uppercase(),
        fontSize = if (item.isUpcoming) 15.sp else 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (item.isUpcoming) IvoryWhite else SoftGray
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Prayer Time
      Text(
        text = item.timeString,
        fontSize = if (item.isUpcoming) 24.sp else 21.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        color = if (item.isUpcoming) Gold400 else IvoryWhite
      )
    }
  }
}
