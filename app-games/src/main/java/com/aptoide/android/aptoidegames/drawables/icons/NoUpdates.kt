package com.aptoide.android.aptoidegames.drawables.icons

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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestNoUpdates() {
  Image(
    imageVector = getNoUpdates(Palette.Primary, Palette.White, Palette.GreyLight),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNoUpdates(
  color1: Color,
  color2: Color,
  color3: Color
): ImageVector = ImageVector.Builder(
  name = "NoUpdates",
  defaultWidth = 328.0.dp,
  defaultHeight = 144.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 144.0f
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
    moveTo(288.0f, 16.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color3),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(252.0f, 83.0f)
    horizontalLineToRelative(8.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-8.0f)
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
    moveTo(76.0f, 8.0f)
    horizontalLineToRelative(8.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-8.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(235.944f, 0.0f)
      curveTo(235.809f, 4.119f, 236.124f, 8.287f, 235.944f, 12.399f)
      curveTo(235.217f, 29.008f, 230.113f, 45.162f, 221.131f, 59.153f)
      curveTo(211.615f, 73.977f, 198.331f, 86.453f, 183.073f, 95.209f)
      lineTo(140.717f, 52.821f)
      curveTo(146.942f, 41.382f, 156.197f, 30.889f, 166.231f, 22.549f)
      curveTo(181.273f, 10.052f, 199.52f, 1.909f, 219.186f, 0.286f)
      lineTo(235.944f, 0.0f)
      close()
      moveTo(190.082f, 30.741f)
      curveTo(172.926f, 32.696f, 175.217f, 59.036f, 192.729f, 57.801f)
      curveTo(210.712f, 56.531f, 208.4f, 28.655f, 190.082f, 30.741f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(92.0f, 144.0f)
      lineTo(108.108f, 115.454f)
      curveTo(113.272f, 108.791f, 117.691f, 94.938f, 127.34f, 94.115f)
      curveTo(138.006f, 93.205f, 145.153f, 104.569f, 139.269f, 113.658f)
      curveTo(137.607f, 116.227f, 134.836f, 117.978f, 132.369f, 119.718f)
      curveTo(123.638f, 125.88f, 114.196f, 131.058f, 105.147f, 136.726f)
      lineTo(92.0f, 144.0f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(174.814f, 99.473f)
      curveTo(169.153f, 101.834f, 163.351f, 103.856f, 157.368f, 105.272f)
      lineTo(130.721f, 78.9f)
      curveTo(131.801f, 72.685f, 134.028f, 66.752f, 136.227f, 60.872f)
      lineTo(174.814f, 99.473f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(147.214f, 131.319f)
      lineTo(152.004f, 112.158f)
      lineTo(155.399f, 115.136f)
      curveTo(164.879f, 111.858f, 174.708f, 109.415f, 183.549f, 104.548f)
      lineTo(180.892f, 114.427f)
      lineTo(147.218f, 131.319f)
      horizontalLineTo(147.214f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(130.869f, 52.698f)
      curveTo(129.221f, 56.386f, 127.417f, 59.993f, 126.076f, 63.822f)
      curveTo(124.149f, 69.328f, 123.041f, 75.113f, 120.782f, 80.509f)
      lineTo(123.825f, 83.972f)
      lineTo(104.67f, 88.765f)
      lineTo(121.439f, 55.239f)
      curveTo(121.622f, 54.879f, 121.943f, 54.808f, 122.286f, 54.674f)
      curveTo(123.076f, 54.367f, 130.548f, 52.398f, 130.869f, 52.698f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(49.0f, 67.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
  }
}.build()
