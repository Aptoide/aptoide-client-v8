package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathBuilder

/**
 * Aptoide Games brand styling for the AppCoins bonus gift icons: the caller's
 * colors are used as-is and the background is a notched rectangle. Vanilla
 * overrides this with a circular brand-orange background and a gold gift.
 */
fun bonusIconBackgroundColor(fallback: Color): Color = fallback

fun bonusIconForegroundColor(fallback: Color): Color = fallback

fun PathBuilder.bonusIconRightOutline() {
  moveTo(0f, 26.6667f)
  verticalLineTo(10.6667f)
  horizontalLineTo(4f)
  verticalLineTo(0f)
  horizontalLineTo(32f)
  verticalLineTo(26.6667f)
  horizontalLineTo(26.6667f)
  verticalLineTo(32f)
  horizontalLineTo(10.6667f)
  verticalLineTo(26.6667f)
  horizontalLineTo(0f)
  close()
}

fun PathBuilder.bonusIconLeftOutline() {
  moveTo(40f, 34f)
  verticalLineTo(13f)
  horizontalLineTo(35f)
  verticalLineTo(0f)
  horizontalLineTo(0f)
  verticalLineTo(34f)
  horizontalLineTo(6f)
  verticalLineTo(40f)
  horizontalLineTo(27f)
  verticalLineTo(34f)
  horizontalLineTo(40f)
  close()
}
