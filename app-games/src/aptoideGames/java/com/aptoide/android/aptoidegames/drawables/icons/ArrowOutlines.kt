package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.ui.graphics.vector.PathBuilder

/**
 * Background outline for the arrow icons in the Aptoide Games brand: a square
 * that fills the 32x32 viewport. The arrow itself is cut out via the EvenOdd
 * fill of the enclosing path. Vanilla provides a circular outline instead.
 */
fun PathBuilder.forwardOutline() {
  moveTo(32.615f, 32.6153f)
  lineTo(-0.6157f, 32.6153f)
  lineTo(-0.6157f, -0.6155f)
  lineTo(32.615f, -0.6155f)
  lineTo(32.615f, 32.6153f)
  close()
}

fun PathBuilder.leftArrowOutline() {
  moveTo(-0.615236f, 32.6154f)
  lineTo(32.6155f, 32.6154f)
  lineTo(32.6155f, -0.61537f)
  lineTo(-0.615231f, -0.615373f)
  lineTo(-0.615236f, 32.6154f)
  close()
}
