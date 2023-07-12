package cm.aptoide.pt.aptoide_ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import cm.aptoide.pt.theme.blueGradientEnd
import cm.aptoide.pt.theme.blueGradientStart
import cm.aptoide.pt.theme.orangeGradientEnd
import cm.aptoide.pt.theme.orangeGradientMid
import cm.aptoide.pt.theme.orangeGradientStart

val orangeGradient = Brush.horizontalGradient(
  colors = listOf(
    orangeGradientStart,
    orangeGradientMid,
    orangeGradientEnd
  )
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

val blueGradient = Brush.linearGradient(
  colors = listOf(blueGradientStart, blueGradientEnd),
  start = Offset(x = 35.3857f, y = 31.6314f),
  end = Offset(x = 403.419f, y = 271.565f)
)
