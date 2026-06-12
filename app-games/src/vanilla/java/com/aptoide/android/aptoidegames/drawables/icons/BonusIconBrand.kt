package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathBuilder

/**
 * Vanilla brand styling for the AppCoins bonus gift icons: a circular
 * background filled with brand orange and a gold gift, regardless of the
 * caller-provided colors. Aptoide Games uses the caller colors and a
 * notched-rectangle background instead.
 */
@Suppress("UNUSED_PARAMETER")
fun bonusIconBackgroundColor(fallback: Color): Color = Color(0xFFFE6446)

@Suppress("UNUSED_PARAMETER")
fun bonusIconForegroundColor(fallback: Color): Color = Color(0xFFF7CB5A)

fun PathBuilder.bonusIconRightOutline() {
  moveTo(16f, 0f)
  curveTo(24.837f, 0f, 32f, 7.163f, 32f, 16f)
  curveTo(32f, 24.837f, 24.837f, 32f, 16f, 32f)
  curveTo(7.163f, 32f, 0f, 24.837f, 0f, 16f)
  curveTo(0f, 7.163f, 7.163f, 0f, 16f, 0f)
  close()
}

fun PathBuilder.bonusIconLeftOutline() {
  moveTo(20f, 0f)
  curveTo(31.046f, 0f, 40f, 8.954f, 40f, 20f)
  curveTo(40f, 31.046f, 31.046f, 40f, 20f, 40f)
  curveTo(8.954f, 40f, 0f, 31.046f, 0f, 20f)
  curveTo(0f, 8.954f, 8.954f, 0f, 20f, 0f)
  close()
}
