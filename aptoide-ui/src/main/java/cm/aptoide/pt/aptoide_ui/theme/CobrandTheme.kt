package cm.aptoide.pt.aptoide_ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.theme.*

object CobrandTheme {
  private val darkMaterialColorPalette = darkColors(
    background = cobrandBlue,
    onBackground = Color.White,
    primary = pinkishOrange,
    primaryVariant = pastelOrange,
    secondary = pinkishOrange,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = blackDarkMode,
    onSurface = greyMedium,
    error = error
  )

  val darkColorPalette = AppColors(
    unselectedLabelColor = greyMedium,
    materialColors = darkMaterialColorPalette
  )

  private val lightMaterialColorPalette = lightColors(
    background = cobrandBlue,
    onBackground = black,
    primary = pinkishOrange,
    primaryVariant = pastelOrange,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = black,
    error = error
  )

  val lightColorPalette = AppColors(
    unselectedLabelColor = grey,
    materialColors = lightMaterialColorPalette
  )


}