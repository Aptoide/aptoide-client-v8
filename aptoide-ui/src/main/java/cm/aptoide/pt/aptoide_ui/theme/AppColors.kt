package cm.aptoide.pt.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class AppColors(
  val unselectedLabelColor: Color,
  val appCoinsColor: Color,
  val greyText: Color,
  val primaryGrey: Color,
  val secondaryGrey: Color,
  val secondBackground: Color,
  val imageIconBackground: Color,
  val downloadViewAppCoinsText: Color,
  val downloadProgressBarBackgroundColor: Color,
  val dividerColor: Color,
  val editorialLabelColor: Color,
  val editorialDateColor: Color,
  val trustedColor: Color,
  val downloadBannerBackgroundColor: Color,
  val appViewTabRowColor: Color,
  val reportAppCardBackgroundColor: Color,
  val reportAppButtonTextColor: Color,
  val storeCardBackgroundColor: Color,
  val storeNumberOfApps: Color,
  val catappultBackgroundColor: Color,
  val materialColors: Colors
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
