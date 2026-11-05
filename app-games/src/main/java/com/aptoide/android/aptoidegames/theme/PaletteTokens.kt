package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.graphics.Color

interface PaletteTokens {
  val Primary: Color
  val Secondary: Color
  val SecondaryLight: Color
  val Error: Color

  val Black: Color
  val GreyDark: Color
  val GameGenieGrey: Color
  val Grey: Color
  val GreyLight: Color
  val White: Color
  val AppCoinsPink: Color

  val Green: Color
  val Official: Color
  val Yellow: Color

  /**
   * Yellow used as text or a thin stroke (tab label, headline). Same as [Yellow100] on dark
   * backgrounds; the vanilla light palette darkens it so it stays readable on white.
   * Yellow fills with dark text on them keep [Yellow100].
   */
  val AccentText: Color

  val Yellow50: Color
  val Yellow100: Color
  val Yellow150: Color
  val Yellow200: Color
  val Orange150: Color
  val Orange200: Color
  val Blue50: Color
  val Blue100: Color
  val Blue150: Color
  val Blue200: Color
  val Blue250: Color
}
