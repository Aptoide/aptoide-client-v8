package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

val blueGradient = Brush.linearGradient(
  colors = listOf(blueGradientStart, blueGradientEnd),
  start = Offset(x = 35.3857f, y = 31.6314f),
  end = Offset(x = 403.419f, y = 271.565f)
)

val lightGradient = Brush.linearGradient(
  colors = listOf(lightGradient2Start, lightGradient2End),
  start = Offset(x = -505.977f, y = -417.283f),
  end = Offset(x = 1031.47f, y = 38.6706f)
)
