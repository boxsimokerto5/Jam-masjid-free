package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MosqueColorScheme = darkColorScheme(
  primary = Gold500,
  onPrimary = Obsidian950,
  primaryContainer = Emerald800,
  onPrimaryContainer = Gold200,
  secondary = Emerald500,
  onSecondary = Obsidian950,
  secondaryContainer = Emerald900,
  onSecondaryContainer = Emerald300,
  tertiary = Gold400,
  background = Obsidian950,
  onBackground = IvoryWhite,
  surface = Obsidian900,
  onSurface = IvoryWhite,
  surfaceVariant = Obsidian800,
  onSurfaceVariant = SoftGray,
  error = CrimsonAlert,
  onError = IvoryWhite
)

@Composable
fun MosqueClockTheme(
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = Obsidian950.toArgb()
        window.navigationBarColor = Obsidian950.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = MosqueColorScheme,
    typography = Typography,
    content = content
  )
}
