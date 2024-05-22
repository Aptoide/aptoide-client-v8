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
fun TestMuted() {
  Image(
    imageVector = getMuted(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getMuted(): ImageVector = ImageVector.Builder(
  name = "Muted",
  defaultWidth = 10.dp,
  defaultHeight = 14.dp,
  viewportWidth = 10f,
  viewportHeight = 14f,
).apply {
  path(
    fill = SolidColor(pureWhite),
  ) {
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
  }
}.build()
