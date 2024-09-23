package com.aptoide.android.aptoidegames.drawables.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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
private fun WantMoreViewFooterPreview() {
  Image(
    imageVector = getWantMoreViewFooter(
      color1 = Color.Green,
      color2 = Color.Black,
      color3 = Color.Magenta,
      color4 = Color.Gray
    ),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getWantMoreViewFooter(
  color1: Color = Palette.Secondary,
  color2: Color = Palette.Black,
  color3: Color = Palette.Primary,
  color4: Color = Palette.Black,
): ImageVector = ImageVector.Builder(
  name = "WantMoreFooter",
  defaultWidth = 360.0.dp,
  defaultHeight = 40.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 40.0f
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
    moveTo(0.0f, 0.0f)
    horizontalLineToRelative(360.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-360.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(247.0f, 34.0f)
    verticalLineTo(13.0f)
    horizontalLineTo(242.125f)
    verticalLineTo(0.0f)
    horizontalLineTo(208.0f)
    verticalLineTo(34.0f)
    horizontalLineTo(213.85f)
    verticalLineTo(40.0f)
    horizontalLineTo(234.325f)
    verticalLineTo(34.0f)
    horizontalLineTo(247.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(236.166f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(218.833f)
      verticalLineTo(18.889f)
      horizontalLineTo(216.666f)
      verticalLineTo(12.222f)
      horizontalLineTo(222.3f)
      curveTo(222.227f, 12.037f, 222.173f, 11.856f, 222.137f, 11.681f)
      curveTo(222.101f, 11.505f, 222.083f, 11.315f, 222.083f, 11.111f)
      curveTo(222.083f, 10.185f, 222.399f, 9.398f, 223.031f, 8.75f)
      curveTo(223.663f, 8.102f, 224.43f, 7.778f, 225.333f, 7.778f)
      curveTo(225.748f, 7.778f, 226.136f, 7.852f, 226.497f, 8.0f)
      curveTo(226.859f, 8.148f, 227.193f, 8.37f, 227.5f, 8.667f)
      curveTo(227.806f, 8.389f, 228.141f, 8.171f, 228.502f, 8.014f)
      curveTo(228.863f, 7.857f, 229.251f, 7.778f, 229.666f, 7.778f)
      curveTo(230.569f, 7.778f, 231.336f, 8.102f, 231.968f, 8.75f)
      curveTo(232.6f, 9.398f, 232.916f, 10.185f, 232.916f, 11.111f)
      curveTo(232.916f, 11.315f, 232.903f, 11.509f, 232.876f, 11.694f)
      curveTo(232.849f, 11.88f, 232.79f, 12.056f, 232.7f, 12.222f)
      horizontalLineTo(238.333f)
      verticalLineTo(18.889f)
      horizontalLineTo(236.166f)
      close()
    }
    path(
      fill = SolidColor(color4),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(236.166f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(238.333f)
      verticalLineTo(12.222f)
      horizontalLineTo(232.7f)
      curveTo(232.79f, 12.056f, 232.849f, 11.88f, 232.876f, 11.694f)
      curveTo(232.903f, 11.509f, 232.916f, 11.315f, 232.916f, 11.111f)
      curveTo(232.916f, 10.185f, 232.6f, 9.398f, 231.968f, 8.75f)
      curveTo(231.336f, 8.102f, 230.569f, 7.778f, 229.666f, 7.778f)
      curveTo(229.251f, 7.778f, 228.863f, 7.857f, 228.502f, 8.014f)
      curveTo(228.141f, 8.171f, 227.806f, 8.389f, 227.5f, 8.667f)
      curveTo(227.193f, 8.37f, 226.859f, 8.148f, 226.497f, 8.0f)
      curveTo(226.136f, 7.852f, 225.748f, 7.778f, 225.333f, 7.778f)
      curveTo(224.43f, 7.778f, 223.663f, 8.102f, 223.031f, 8.75f)
      curveTo(222.399f, 9.398f, 222.083f, 10.185f, 222.083f, 11.111f)
      curveTo(222.083f, 11.315f, 222.101f, 11.505f, 222.137f, 11.681f)
      curveTo(222.173f, 11.856f, 222.227f, 12.037f, 222.3f, 12.222f)
      horizontalLineTo(216.666f)
      verticalLineTo(18.889f)
      horizontalLineTo(218.833f)
      verticalLineTo(31.111f)
      horizontalLineTo(236.166f)
      close()
      moveTo(225.333f, 10.0f)
      curveTo(225.64f, 10.0f, 225.897f, 10.106f, 226.105f, 10.319f)
      curveTo(226.312f, 10.533f, 226.416f, 10.796f, 226.416f, 11.111f)
      curveTo(226.416f, 11.426f, 226.312f, 11.69f, 226.105f, 11.903f)
      curveTo(225.897f, 12.116f, 225.64f, 12.222f, 225.333f, 12.222f)
      curveTo(225.026f, 12.222f, 224.769f, 12.116f, 224.561f, 11.903f)
      curveTo(224.353f, 11.69f, 224.25f, 11.426f, 224.25f, 11.111f)
      curveTo(224.25f, 10.796f, 224.353f, 10.533f, 224.561f, 10.319f)
      curveTo(224.769f, 10.106f, 225.026f, 10.0f, 225.333f, 10.0f)
      close()
      moveTo(230.75f, 11.111f)
      curveTo(230.75f, 11.426f, 230.646f, 11.69f, 230.438f, 11.903f)
      curveTo(230.23f, 12.116f, 229.973f, 12.222f, 229.666f, 12.222f)
      curveTo(229.359f, 12.222f, 229.102f, 12.116f, 228.894f, 11.903f)
      curveTo(228.687f, 11.69f, 228.583f, 11.426f, 228.583f, 11.111f)
      curveTo(228.583f, 10.796f, 228.687f, 10.533f, 228.894f, 10.319f)
      curveTo(229.102f, 10.106f, 229.359f, 10.0f, 229.666f, 10.0f)
      curveTo(229.973f, 10.0f, 230.23f, 10.106f, 230.438f, 10.319f)
      curveTo(230.646f, 10.533f, 230.75f, 10.796f, 230.75f, 11.111f)
      close()
      moveTo(236.166f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(228.583f)
      verticalLineTo(14.444f)
      horizontalLineTo(236.166f)
      close()
      moveTo(228.583f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(234.0f)
      verticalLineTo(28.889f)
      horizontalLineTo(228.583f)
      close()
      moveTo(226.416f, 28.889f)
      horizontalLineTo(221.0f)
      verticalLineTo(18.889f)
      horizontalLineTo(226.416f)
      verticalLineTo(28.889f)
      close()
      moveTo(218.833f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(226.416f)
      verticalLineTo(16.667f)
      horizontalLineTo(218.833f)
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
      moveTo(88.0f, 8.0f)
      horizontalLineToRelative(83.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-83.0f)
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
      moveTo(360.0f, 0.0f)
      lineToRelative(-32.0f, 0.0f)
      lineToRelative(-0.0f, 24.0f)
      lineToRelative(32.0f, 0.0f)
      close()
    }
  }
}.build()
