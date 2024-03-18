package cm.aptoide.pt.app_games.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush


val ghLogoGradient = Brush.linearGradient(
  colors = listOf(magentaGradientStart, magentaGradientEnd),
  start = Offset(x = 162.123f, y = 3.53846f),
  end = Offset(x = 152.039f, y = 33.9935f)
)

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
