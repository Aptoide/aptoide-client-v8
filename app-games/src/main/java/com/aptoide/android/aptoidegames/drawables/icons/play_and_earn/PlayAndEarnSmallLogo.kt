package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestPaESmallLogo() {
  Image(
    imageVector = getPaESmallLogo(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaESmallLogo(
  color1: Color = Palette.White,
  color2: Color = Palette.Secondary
): ImageVector = ImageVector.Builder(
  name = "PlayAndEarnSmallLogo",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(11.999f, 0.511f)
    lineTo(20.123f, 3.876f)
    lineTo(23.488f, 12.0f)
    lineTo(20.123f, 20.124f)
    lineTo(11.999f, 23.489f)
    lineTo(3.875f, 20.124f)
    lineTo(0.51f, 12.0f)
    lineTo(3.875f, 3.876f)
    lineTo(11.999f, 0.511f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(15.338f, 12.245f)
    lineTo(12.447f, 11.478f)
    lineTo(13.883f, 9.027f)
    curveTo(14.01f, 8.776f, 14.025f, 8.499f, 13.936f, 8.24f)
    lineTo(12.709f, 5.336f)
    curveTo(12.625f, 5.147f, 12.483f, 5.108f, 12.349f, 5.233f)
    lineTo(8.152f, 11.629f)
    lineTo(8.661f, 11.755f)
    lineTo(11.552f, 12.522f)
    lineTo(10.116f, 14.973f)
    curveTo(9.989f, 15.224f, 9.974f, 15.501f, 10.064f, 15.76f)
    lineTo(11.29f, 18.664f)
    curveTo(11.374f, 18.854f, 11.516f, 18.892f, 11.65f, 18.767f)
    lineTo(15.847f, 12.371f)
    lineTo(15.338f, 12.245f)
    close()
  }
}.build()
