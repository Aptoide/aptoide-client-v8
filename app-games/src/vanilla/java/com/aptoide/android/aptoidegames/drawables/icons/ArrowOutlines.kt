package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.ui.graphics.vector.PathBuilder

/**
 * Background outline for the arrow icons in the Vanilla brand: a circle that
 * fills the 32x32 viewport. The arrow itself is cut out via the EvenOdd fill
 * of the enclosing path. Aptoide Games provides a rectangular outline instead.
 */
fun PathBuilder.forwardOutline() {
  moveTo(16f, 0f)
  curveTo(24.837f, 0f, 32f, 7.163f, 32f, 16f)
  curveTo(32f, 24.837f, 24.837f, 32f, 16f, 32f)
  curveTo(7.163f, 32f, 0f, 24.837f, 0f, 16f)
  curveTo(0f, 7.163f, 7.163f, 0f, 16f, 0f)
  close()
}

fun PathBuilder.leftArrowOutline() {
  moveTo(16f, 0f)
  curveTo(24.837f, 0f, 32f, 7.163f, 32f, 16f)
  curveTo(32f, 24.837f, 24.837f, 32f, 16f, 32f)
  curveTo(7.163f, 32f, 0f, 24.837f, 0f, 16f)
  curveTo(0f, 7.163f, 7.163f, 0f, 16f, 0f)
  close()
}
