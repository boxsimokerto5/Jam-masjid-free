package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarqueeTextBanner(
  texts: List<String>,
  modifier: Modifier = Modifier,
  velocityDp: Int = 45
) {
  val fullText = if (texts.isEmpty()) {
    "Selamat Datang di Rumah Allah ✦ Lurus dan Rapatkan Shaf ✦"
  } else {
    texts.joinToString("   ✦   ") + "   ✦   "
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Black.copy(alpha = 0.85f))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Speaker Badge
    Box(
      modifier = Modifier
        .background(Emerald800, shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Campaign,
          contentDescription = "Pengumuman",
          tint = Gold400,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "INFO",
          color = Gold400,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 4.dp)
        )
      }
    }

    // Running Marquee
    Box(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 12.dp)
    ) {
      Text(
        text = fullText,
        color = IvoryWhite,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        modifier = Modifier.basicMarquee(
          animationMode = MarqueeAnimationMode.Immediately,
          iterations = Int.MAX_VALUE,
          velocity = velocityDp.dp
        )
      )
    }
  }
}
