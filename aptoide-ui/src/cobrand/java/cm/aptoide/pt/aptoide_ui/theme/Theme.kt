package cm.aptoide.pt.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.aptoide_ui.BuildConfig
import cm.aptoide.pt.aptoide_ui.theme.AptoideBaseTheme
import cm.aptoide.pt.aptoide_ui.theme.CobrandTheme

private val darkMaterialColorPalette = darkColors(
  background = black,
  onBackground = Color.White,
  primary = cobrandGreen,
  primaryVariant = pastelOrange,
  secondary = pinkishOrange,
  onPrimary = Color.White,
  onSecondary = Color.White,
  surface = blackDarkMode,
  onSurface = greyMedium,
  error = error
)

val darkColorPalette = AppColors(
  unselectedLabelColor = greyMedium, appCoinsColor = appCoins,
  greyText = greyMedium,
  downloadViewAppCoinsText = Color.White,
  downloadProgressBarBackgroundColor = grey,
  materialColors = darkMaterialColorPalette
)

private val lightMaterialColorPalette = lightColors(
  background = Color.White,
  onBackground = black,
  primary = cobrandGreen,
  primaryVariant = pastelOrange,
  onPrimary = Color.White,
  onSecondary = Color.White,
  surface = Color.White,
  onSurface = black,
  error = error
)

val lightColorPalette = AppColors(
  unselectedLabelColor = grey,
  appCoinsColor = appCoins, greyText = grey,
  downloadViewAppCoinsText = appCoins,
  downloadProgressBarBackgroundColor = greyLight,
  materialColors = lightMaterialColorPalette
)

object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
}

private val LocalAppColors = staticCompositionLocalOf {
  lightColorPalette //default
}

@Composable
fun AptoideTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) darkColorPalette else lightColorPalette
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

