package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray
import java.util.Locale

@Composable
fun AdzanAlertOverlay(
  prayerName: String,
  secondsLeft: Int,
  onDismiss: () -> Unit,
  onStartIqomahNow: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "adzan_flash")
  val flashAlpha by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flash_alpha"
  )
  val bellScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bell_scale"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.92f))
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Emerald800.copy(alpha = 0.95f),
              Color(0xFF032B1A),
              Obsidian950
            )
          ),
          shape = RoundedCornerShape(24.dp)
        )
        .border(
          width = 3.dp,
          color = Gold400.copy(alpha = flashAlpha),
          shape = RoundedCornerShape(24.dp)
        )
        .padding(32.dp)
    ) {
      Icon(
        imageVector = Icons.Default.NotificationsActive,
        contentDescription = "Adzan",
        tint = Gold400,
        modifier = Modifier
          .size(72.dp)
          .scale(bellScale)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "WAKTU ADZAN TELAH TIBA",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Gold400,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = prayerName.uppercase(),
        fontSize = 44.sp,
        fontWeight = FontWeight.ExtraBold,
        color = IvoryWhite,
        letterSpacing = 4.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Kumandangkan adzan dan laksanakan sholat sunnah qobliyah",
        fontSize = 15.sp,
        color = SoftGray,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      val min = secondsLeft / 60
      val sec = secondsLeft % 60
      Text(
        text = String.format(Locale.US, "Iqomah dimulai dalam: %02d:%02d", min, sec),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Emerald500
      )

      Spacer(modifier = Modifier.height(24.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
          onClick = onStartIqomahNow,
          colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black)
        ) {
          Text("Mulai Iqomah Sekarang", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onDismiss,
          border = androidx.compose.foundation.BorderStroke(1.dp, SoftGray)
        ) {
          Text("Tutup", color = IvoryWhite)
        }
      }
    }
  }
}

@Composable
fun IqomahCountdownOverlay(
  prayerName: String,
  secondsLeft: Int,
  onDismiss: () -> Unit,
  onStartSholatNow: () -> Unit
) {
  val isUrgent = secondsLeft <= 10

  val min = secondsLeft / 60
  val sec = secondsLeft % 60
  val timeDisplay = String.format(Locale.US, "%02d:%02d", min, sec)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.94f))
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              if (isUrgent) CrimsonAlert.copy(alpha = 0.4f) else Emerald800.copy(alpha = 0.8f),
              Obsidian950
            )
          ),
          shape = RoundedCornerShape(24.dp)
        )
        .border(
          width = 3.dp,
          color = if (isUrgent) CrimsonAlert else Gold400,
          shape = RoundedCornerShape(24.dp)
        )
        .padding(32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HourglassTop,
            contentDescription = null,
            tint = Gold400,
            modifier = Modifier.size(28.dp)
          )
          Text(
            text = "HITUNG MUNDUR IQOMAH",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gold400,
            modifier = Modifier.padding(start = 8.dp)
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "SHOLAT ${prayerName.uppercase()}",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = IvoryWhite
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Giant Countdown Digits
      Box(
        modifier = Modifier
          .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
          .border(
            width = 2.dp,
            color = if (isUrgent) CrimsonAlert else Emerald500,
            shape = RoundedCornerShape(16.dp)
          )
          .padding(horizontal = 36.dp, vertical = 16.dp)
      ) {
        Text(
          text = timeDisplay,
          fontSize = 72.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = if (isUrgent) CrimsonAlert else Gold400
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isUrgent) "IQOMAH SEGERA DIKUMANDANGKAN!" else "Persiapkan diri untuk merapatkan shaf sholat",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isUrgent) CrimsonAlert else SoftGray
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onStartSholatNow,
        colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.Black)
      ) {
        Text("Mulai Mode Sholat Berjamaah", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun SholatBlankScreen(
  minutesLeft: Int,
  onDismiss: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "sholat_breathe")
  val breathAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath_alpha"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .clickable { onDismiss() }
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = Icons.Default.Mosque,
        contentDescription = "Sholat",
        tint = Gold400,
        modifier = Modifier
          .size(80.dp)
          .alpha(breathAlpha)
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "LURUS DAN RAPATKAN SHAF",
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = IvoryWhite,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "سَوُّوا صُفُوفَكُمْ فَإِنَّ تَسْوِيَةَ الصَّفِّ مِنْ تَمَامِ الصَّلَاةِ",
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        color = Gold400,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .background(Color(0xFF15181F), shape = RoundedCornerShape(12.dp))
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PhonelinkRing,
          contentDescription = "Silent phone",
          tint = CrimsonAlert,
          modifier = Modifier.size(24.dp)
        )
        Text(
          text = "Mohon Nonaktifkan / Senyapkan Nada Dering Handphone",
          color = SoftGray,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(start = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "Layar hening selama sholat berjamaah (${minutesLeft} menit tersisa)",
        fontSize = 13.sp,
        color = Color.DarkGray
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Ketuk layar di mana saja untuk kembali",
        fontSize = 12.sp,
        color = Color.Gray.copy(alpha = 0.5f)
      )
    }
  }
}
