package cm.aptoide.pt.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import cm.aptoide.pt.aptoide_ui.BuildConfig
import cm.aptoide.pt.aptoide_ui.theme.AptoideBaseTheme
import cm.aptoide.pt.aptoide_ui.theme.CobrandTheme


object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
}

private val LocalAppColors = staticCompositionLocalOf {
  AptoideBaseTheme.lightColorPalette //default
}

@Composable
fun AptoideTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  baseAppTheme: String = getBaseAppTheme(),
  content: @Composable () -> Unit
) {
  val colors = getThemeColors(baseAppTheme, darkTheme)
  val typography = if (darkTheme) darkTypography else lightTypography

  CompositionLocalProvider(
    LocalAppColors provides colors,
  ) {
    MaterialTheme(
      colors = colors.materialColors,
      typography = typography,
      shapes = shapes,
      content = content
    )
  }
}

fun getBaseAppTheme(): String {
  return BuildConfig.APTOIDE_THEME
}

private fun getThemeColors(baseTheme: String, isDarkTheme: Boolean): AppColors {
  return if (baseTheme == "cobrand") {
    if (isDarkTheme) CobrandTheme.darkColorPalette else CobrandTheme.lightColorPalette
  } else {
    if (isDarkTheme) AptoideBaseTheme.darkColorPalette else AptoideBaseTheme.lightColorPalette
  }
}

