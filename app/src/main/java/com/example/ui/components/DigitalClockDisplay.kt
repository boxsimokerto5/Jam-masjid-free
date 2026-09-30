package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian900

@Composable
fun DigitalClockDisplay(
  timeFormatted: String,      // "15:42"
  secondsFormatted: String,   // "38"
  modifier: Modifier = Modifier,
  timeFontSize: TextUnit = 56.sp,
  secondsFontSize: TextUnit = 22.sp
) {
  val infiniteTransition = rememberInfiniteTransition(label = "colon_blink")
  val colonAlpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "colon_alpha"
  )

  val parts = timeFormatted.split(":")
  val hourPart = parts.getOrNull(0) ?: "00"
  val minPart = parts.getOrNull(1) ?: "00"

  Box(
    modifier = modifier
      .background(
        color = Obsidian900.copy(alpha = 0.75f),
        shape = RoundedCornerShape(16.dp)
      )
      .border(
        width = 1.5.dp,
        color = Gold500.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp)
      )
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Hour
      Text(
        text = hourPart,
        fontSize = timeFontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = IvoryWhite,
        letterSpacing = 1.sp
      )

      // Blinking Colon
      Text(
        text = ":",
        fontSize = timeFontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = Gold400,
        modifier = Modifier.alpha(colonAlpha)
      )

      // Minute
      Text(
        text = minPart,
        fontSize = timeFontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = IvoryWhite,
        letterSpacing = 1.sp
      )

      // Seconds Badge
      Box(
        modifier = Modifier
          .padding(start = 10.dp)
          .background(
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(8.dp)
          )
          .border(
            width = 1.dp,
            color = Emerald500.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "DETIK",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Emerald500
          )
          Text(
            text = secondsFormatted,
            fontSize = secondsFontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Gold400
          )
        }
      }
    }
  }
}
