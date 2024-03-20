package cm.aptoide.pt.aptoide_ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

val orangeGradient = Brush.linearGradient(
  colors = listOf(
    orangeGradientStart,
    pinkishOrange
  ),
  start = Offset(x = 29.2612f, y = 5.5355f),
  end = Offset(x = 70.6041f, y = 132.895f)
)

val appCoinsGradient = Brush.linearGradient(
  colors = listOf(
    orangeGradientStart,
    orangeGradientMid,
    orangeGradientEnd
  ),
  start = Offset(x = 1.029f, y = 1.44f),
  end = Offset(x = 28.42f, y = 15.203f)
)

val appCoinsButtonGradient = Brush.horizontalGradient(
  colorStops = arrayOf(
    0.0f to orangeGradientStart,
    0.192708f to orangeGradientMid,
    0.760417f to orangeGradientEnd
  ),
)

val blueGradient = Brush.linearGradient(
  colors = listOf(blueGradientStart, blueGradientEnd),
  start = Offset(x = 35.3857f, y = 31.6314f),
  end = Offset(x = 403.419f, y = 271.565f)
)
