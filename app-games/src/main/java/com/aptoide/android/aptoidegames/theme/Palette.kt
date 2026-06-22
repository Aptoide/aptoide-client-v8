package com.aptoide.android.aptoidegames.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalPalette = staticCompositionLocalOf<PaletteTokens> { FallbackPalette }

val Palette: PaletteTokens
  @Composable
  @ReadOnlyComposable
  get() = LocalPalette.current

private object FallbackPalette : PaletteTokens {
  override val Primary = Color(0xFFC8ED4F)
  override val Secondary = Color(0xFF913DD8)
  override val SecondaryLight = Color(0xFFCA8BFF)
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
