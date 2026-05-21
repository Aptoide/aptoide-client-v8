package com.aptoide.android.aptoidegames.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

@Suppress("unused")
object AppTheme {
  val isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalDarkTheme.current
}

private val LocalDarkTheme = staticCompositionLocalOf { true }

@Composable
fun AptoideTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val palette: PaletteTokens = DarkPalette
  SetupStatusBarColor(palette.Black, darkTheme = true)

  CompositionLocalProvider(
    LocalDarkTheme provides true,
    LocalPalette provides palette,
  ) {
    MaterialTheme(
      colors = darkColors(
        background = palette.Black,
        onBackground = palette.White,
        primary = palette.Primary,
        secondary = palette.Secondary,
        onPrimary = palette.Black,
        onSecondary = palette.White,
        surface = palette.Black,
        onSurface = palette.White,
        error = palette.Error,
      ),
      typography = Typography(
        h1 = AGTypography.Title,
        h2 = AGTypography.InputsL,
        body1 = AGTypography.DescriptionGames,
        body2 = AGTypography.DescriptionGames
      ),
      content = content
    )
  }
}

@Composable
private fun SetupStatusBarColor(
  backgroundColor: Color,
  darkTheme: Boolean,
) {
  val context = LocalContext.current
  LaunchedEffect(key1 = darkTheme) {
    context.let { if (it is Activity) it else null }
      ?.window
      ?.run {
        statusBarColor = backgroundColor.toArgb()
        WindowCompat.getInsetsController(this, decorView)
          .isAppearanceLightStatusBars = !darkTheme
      }
  }
}
