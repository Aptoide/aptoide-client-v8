package cm.aptoide.pt.app_games.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class AppColors(
  val moreAppsViewSeparatorColor: Color,
  val materialColors: Colors,
  val dividerColor: Color,
  val unselectedLabelColor: Color,
  ) {
  val primary: Color
    get() = materialColors.primary
  val primaryVariant: Color
    get() = materialColors.primaryVariant
  val secondary: Color
    get() = materialColors.secondary
  val secondaryVariant: Color
    get() = materialColors.secondaryVariant
  val background: Color
    get() = materialColors.background
  val surface: Color
    get() = materialColors.surface
  val error: Color
    get() = materialColors.error
  val onPrimary: Color
    get() = materialColors.onPrimary
  val onSecondary: Color
    get() = materialColors.onSecondary
  val onBackground: Color
    get() = materialColors.onBackground
  val onSurface: Color
    get() = materialColors.onSurface
  val onError: Color
    get() = materialColors.onError
  val isLight: Boolean
    get() = materialColors.isLight
}
