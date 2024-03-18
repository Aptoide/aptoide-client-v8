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
  val gameTitleTextCondensed: TextStyle,

  )
