package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold900
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950

@Composable
fun MosqueBackground(
  backgroundType: String,
  customBackgroundUri: String,
  overlayDarkness: Float,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    // 1. Base Layer (Image or Procedural Gradient)
    when {
      backgroundType == "custom_uri" && customBackgroundUri.isNotBlank() -> {
        AsyncImage(
          model = Uri.parse(customBackgroundUri),
          contentDescription = "Custom Mosque Background",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      backgroundType == "preset_twilight" -> {
        Image(
          painter = painterResource(id = R.drawable.img_mosque_twilight),
          contentDescription = "Mosque Twilight Background",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      backgroundType == "preset_emerald" -> {
        Image(
          painter = painterResource(id = R.drawable.img_mosque_emerald),
          contentDescription = "Mosque Emerald Interior Background",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      backgroundType == "gradient_emerald" -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.radialGradient(
                colors = listOf(
                  Color(0xFF0D5C3A),
                  Emerald900,
                  Color(0xFF02170E),
                  Obsidian950
                )
              )
            )
        )
      }

      backgroundType == "gradient_midnight" -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF0F1E36),
                  Color(0xFF08101E),
                  Color(0xFF03070E)
                )
              )
            )
        )
      }

      backgroundType == "gradient_sunset" -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF3B1D11),
                  Gold900,
                  Color(0xFF1F1206),
                  Obsidian950
                )
              )
            )
        )
      }

      else -> {
        // Pure Obsidian Carbon
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Obsidian900,
                  Obsidian950,
                  Color(0xFF020305)
                )
              )
            )
        )
      }
    }

    // 2. Custom Darkness Overlay Veil (Adjustable from 0.1 to 0.9)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = overlayDarkness.coerceIn(0.1f, 0.9f)))
    )

    // 3. Subtle Vignette & Gradient Edges for high-contrast visibility
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.55f),
              Color.Transparent,
              Color.Black.copy(alpha = 0.75f)
            )
          )
        )
    )

    // 4. Foreground Content
    content()
  }
}
