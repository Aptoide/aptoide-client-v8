package com.aptoide.android.aptoidegames.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle

data class AppTypography(
  val materialTypography: Typography,

  //AptoideGames typography
  val title: TextStyle,
  val inputs_L: TextStyle,
  val inputs_M: TextStyle,
  val inputs_S: TextStyle,
  val articleText: TextStyle,
  val body: TextStyle,
  val titleGames: TextStyle,
  val subHeading_M: TextStyle,
  val subHeading_S: TextStyle,
  val descriptionGames: TextStyle,
  val smallGames: TextStyle,

  // DT typography
  val bodyCopy: TextStyle,
  val bodyCopyXS: TextStyle,
  val bodyCopyBold: TextStyle,
  val bodyCopySmall: TextStyle,
  val bodyCopySmallBold: TextStyle,
  val headlineTitleText: TextStyle,
  val headlineTitleTextSecondary: TextStyle,
  val buttonTextSmall: TextStyle,
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
