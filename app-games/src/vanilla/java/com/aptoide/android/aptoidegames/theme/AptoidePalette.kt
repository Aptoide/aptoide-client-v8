package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.graphics.Color

object DarkPalette : PaletteTokens {
  override val Primary = Color(0xFFFE6446)
  override val Secondary = Color(0xFFFE9150)
  override val SecondaryLight = Color(0xFFFFB58A)
  override val Error = Color(0xFFFF0000)

  override val Black = Color(0xFF1E1E26)
  override val GreyDark = Color(0xFF312D35)
  override val GameGenieGrey = Color(0xFF2D3224)
  override val Grey = Color(0xFF595959)
  override val GreyLight = Color(0xFFD2D2D2)
  override val White = Color(0xFFFFFFFF)
  override val AppCoinsPink = Color(0xFFFF6381)

  override val Green = Color(0xFF25B47E)
  override val Official = Color(0xFF007AFF)
  override val Yellow = Color(0xFFFFC93E)

  override val Yellow50 = Color(0xFFFFEA04)
  override val Yellow100 = Color(0xFFFFC93E)
  override val Yellow150 = Color(0xFFD6A422)
  override val Yellow200 = Color(0xFF876311)
  override val Orange150 = Color(0xFFF58932)
  override val Orange200 = Color(0xFFC04D07)
  override val Blue50 = Color(0xFFE0ECFF)
  override val Blue100 = Color(0xFFBDC3EB)
  override val Blue150 = Color(0xFFB3CFFF)
  override val Blue200 = Color(0xFF676D89)
  override val Blue250 = Color(0xFF495A6D)
}

object LightPalette : PaletteTokens {
  override val Primary = DarkPalette.Primary
  override val Secondary = DarkPalette.Secondary
  override val SecondaryLight = DarkPalette.SecondaryLight
  override val Error = DarkPalette.Error

  override val Black = Color(0xFFFFFFFF)
  override val GreyDark = Color(0xFFF5F5F5)
  override val GameGenieGrey = Color(0xFFF0F4E8)
  override val Grey = Color(0xFF595959)
  override val GreyLight = Color(0xFF313131)
  override val White = Color(0xFF1E1E26)
  override val AppCoinsPink = DarkPalette.AppCoinsPink

  override val Green = DarkPalette.Green
  override val Official = DarkPalette.Official
  override val Yellow = DarkPalette.Yellow

  override val Yellow50 = DarkPalette.Yellow50
  override val Yellow100 = DarkPalette.Yellow100
  override val Yellow150 = DarkPalette.Yellow150
  override val Yellow200 = DarkPalette.Yellow200
  override val Orange150 = DarkPalette.Orange150
  override val Orange200 = DarkPalette.Orange200
  override val Blue50 = DarkPalette.Blue50
  override val Blue100 = DarkPalette.Blue100
  override val Blue150 = DarkPalette.Blue150
  override val Blue200 = DarkPalette.Blue200
  override val Blue250 = DarkPalette.Blue250
}
