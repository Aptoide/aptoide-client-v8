package cm.aptoide.pt.aptoide_ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.theme.*

object CobrandTheme {
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


}