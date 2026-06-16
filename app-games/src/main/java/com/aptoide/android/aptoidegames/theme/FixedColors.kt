package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.graphics.Color

/**
 * Theme-invariant brand colors. Unlike [Palette] (a @Composable facade whose
 * Black/White INVERT between Vanilla's light and dark themes), these are the
 * same in every theme/brand. Use for elements that must keep a fixed color
 * regardless of theme: text/icons on the brand Primary color, AppCoins gift
 * outlines, and labels/overlays drawn on images. Being a plain object, it is
 * also usable from non-composable code (ImageVector/PathBuilder/Notification
 * builders) where Palette cannot be called.
 */
object FixedColors {
  val Dark = Color(0xFF1E1E26)          // dark on Primary / icon outlines
  val White = Color(0xFFFFFFFF)         // white labels/overlays on images
  val Scrim = Color(0xFF000000)         // image-overlay scrim
  val VanillaOrange = Color(0xFFFE6446) // vanilla brand accent (vanilla branches only)
  val VanillaGiftGold = Color(0xFFF7CB5A)
}
