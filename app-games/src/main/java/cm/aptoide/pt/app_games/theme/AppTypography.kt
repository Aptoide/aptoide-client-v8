package cm.aptoide.pt.app_games.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle

data class AppTypography(
  val materialTypography: Typography,

  // DT typography
  val bodyCopyXS: TextStyle,
  val bodyCopySmall: TextStyle,
  val bodyCopySmallBold: TextStyle,
  val headlineTitleText: TextStyle,
  val headlineTitleTextSecondary: TextStyle,
  val buttonTextLight: TextStyle,
  val buttonTextMedium: TextStyle,
  val gameTitleTextCondensedSmall: TextStyle,
  val gameTitleTextCondensed: TextStyle,
  val gameTitleTextCondensedLarge: TextStyle,
  val gameTitleTextCondensedXL: TextStyle,
  val medium_S: TextStyle,
  val medium_XS: TextStyle,
  val button_M: TextStyle,
  val button_L: TextStyle,
)
