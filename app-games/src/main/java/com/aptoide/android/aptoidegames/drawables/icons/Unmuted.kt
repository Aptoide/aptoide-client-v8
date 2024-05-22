package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.pureWhite

@Preview
@Composable
fun TestUnmuted() {
  Image(
    imageVector = getUnmuted(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getUnmuted(): ImageVector = ImageVector.Builder(
  name = "Unmuted",
  defaultWidth = 17.dp,
  defaultHeight = 14.dp,
  viewportWidth = 17f,
  viewportHeight = 14f,
).apply {
  path(
    fill = SolidColor(pureWhite),
  ) {
    moveTo(11.8812f, 13.7332f)
    curveTo(11.6698f, 13.7986f, 11.4736f, 13.7684f, 11.3025f, 13.6325f)
    curveTo(11.1314f, 13.4967f, 11.0458f, 13.3205f, 11.0458f, 13.0941f)
    curveTo(11.0458f, 12.9884f, 11.076f, 12.8928f, 11.1364f, 12.8072f)
    curveTo(11.1968f, 12.7217f, 11.2773f, 12.6663f, 11.383f, 12.6361f)
    curveTo(12.5908f, 12.2134f, 13.567f, 11.4888f, 14.3118f, 10.4622f)
    curveTo(15.0566f, 9.43562f, 15.429f, 8.28323f, 15.429f, 6.99497f)
    curveTo(15.429f, 5.70671f, 15.0566f, 4.54928f, 14.3118f, 3.51767f)
    curveTo(13.567f, 2.48605f, 12.5908f, 1.76643f, 11.383f, 1.35379f)
    curveTo(11.2773f, 1.32863f, 11.1918f, 1.26824f, 11.1364f, 1.17262f)
    curveTo(11.0811f, 1.07701f, 11.0458f, 0.981399f, 11.0458f, 0.875721f)
    curveTo(11.0458f, 0.649268f, 11.1364f, 0.473139f, 11.3126f, 0.347332f)
    curveTo(11.4887f, 0.221525f, 11.6799f, 0.191331f, 11.8812f, 0.256751f)
    curveTo(13.3003f, 0.759978f, 14.4477f, 1.63056f, 15.3182f, 2.85844f)
    curveTo(16.1888f, 4.08631f, 16.6216f, 5.46516f, 16.6216f, 6.99497f)
    curveTo(16.6216f, 8.52478f, 16.1888f, 9.89859f, 15.3182f, 11.1315f)
    curveTo(14.4477f, 12.3644f, 13.3003f, 13.2249f, 11.8812f, 13.7332f)
    close()
    moveTo(0.759874f, 10.0194f)
    curveTo(0.543486f, 10.0194f, 0.362324f, 9.94891f, 0.216388f, 9.80298f)
    curveTo(0.0754841f, 9.66207f, 0f, 9.48091f, 0f, 9.25949f)
    verticalLineTo(4.73044f)
    curveTo(0f, 4.51406f, 0.0704519f, 4.33289f, 0.216388f, 4.18696f)
    curveTo(0.357292f, 4.04605f, 0.538454f, 3.97057f, 0.759874f, 3.97057f)
    horizontalLineTo(4.03085f)
    lineTo(7.7799f, 0.23159f)
    curveTo(8.01642f, -0.00492742f, 8.28816f, -0.0602825f, 8.60016f, 0.0655244f)
    curveTo(8.91216f, 0.191331f, 9.06816f, 0.422816f, 9.06816f, 0.759978f)
    verticalLineTo(13.24f)
    curveTo(9.06816f, 13.5772f, 8.91216f, 13.8087f, 8.60016f, 13.9345f)
    curveTo(8.28816f, 14.0603f, 8.01642f, 14.0049f, 7.7799f, 13.7684f)
    lineTo(4.03085f, 10.0194f)
    horizontalLineTo(0.759874f)
    close()
    moveTo(10.6483f, 10.0747f)
    verticalLineTo(3.94038f)
    curveTo(10.6483f, 3.79947f, 10.7892f, 3.69379f, 10.92f, 3.74915f)
    curveTo(11.5138f, 3.9907f, 11.9969f, 4.38322f, 12.3794f, 4.9267f)
    curveTo(12.8172f, 5.5507f, 13.0386f, 6.25019f, 13.0386f, 7.02013f)
    curveTo(13.0386f, 7.79007f, 12.8172f, 8.49962f, 12.3794f, 9.11355f)
    curveTo(11.9969f, 9.64194f, 11.5138f, 10.0294f, 10.92f, 10.271f)
    curveTo(10.7892f, 10.3263f, 10.6433f, 10.2207f, 10.6433f, 10.0798f)
    lineTo(10.6483f, 10.0747f)
    close()
  }
}.build()
