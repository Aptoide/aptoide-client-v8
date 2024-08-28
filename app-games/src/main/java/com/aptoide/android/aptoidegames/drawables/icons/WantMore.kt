package com.aptoide.android.aptoidegames.drawables.icons

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

@Preview
@Composable
private fun WantMorePreview() {
  Image(
    imageVector = getWantMore(Color.Green, Color.Black, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getWantMore(
  iconColor: Color,
  outlineColor: Color,
  backgroundColor: Color,
  themeColor: Color,
): ImageVector = ImageVector.Builder(
  name = "Wantmore",
  defaultWidth = 360.0.dp,
  defaultHeight = 176.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 176.0f
).apply {
  path(
    fill = SolidColor(backgroundColor), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 16.0f)
    horizontalLineToRelative(360.0f)
    verticalLineToRelative(144.0f)
    horizontalLineToRelative(-360.0f)
    close()
  }
  path(
    fill = SolidColor(backgroundColor), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(336.0f, 34.0f)
    verticalLineTo(13.0f)
    horizontalLineTo(331.0f)
    verticalLineTo(0.0f)
    horizontalLineTo(296.0f)
    verticalLineTo(34.0f)
    horizontalLineTo(302.0f)
    verticalLineTo(40.0f)
    horizontalLineTo(323.0f)
    verticalLineTo(34.0f)
    horizontalLineTo(336.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(iconColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(324.889f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(307.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(304.889f)
      verticalLineTo(12.222f)
      horizontalLineTo(310.667f)
      curveTo(310.593f, 12.037f, 310.537f, 11.856f, 310.5f, 11.681f)
      curveTo(310.463f, 11.505f, 310.445f, 11.315f, 310.445f, 11.111f)
      curveTo(310.445f, 10.185f, 310.769f, 9.398f, 311.417f, 8.75f)
      curveTo(312.065f, 8.102f, 312.852f, 7.778f, 313.778f, 7.778f)
      curveTo(314.204f, 7.778f, 314.602f, 7.852f, 314.972f, 8.0f)
      curveTo(315.343f, 8.148f, 315.685f, 8.37f, 316.0f, 8.667f)
      curveTo(316.315f, 8.389f, 316.658f, 8.171f, 317.028f, 8.014f)
      curveTo(317.398f, 7.857f, 317.797f, 7.778f, 318.222f, 7.778f)
      curveTo(319.148f, 7.778f, 319.935f, 8.102f, 320.584f, 8.75f)
      curveTo(321.232f, 9.398f, 321.556f, 10.185f, 321.556f, 11.111f)
      curveTo(321.556f, 11.315f, 321.542f, 11.509f, 321.514f, 11.694f)
      curveTo(321.486f, 11.88f, 321.426f, 12.056f, 321.334f, 12.222f)
      horizontalLineTo(327.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(324.889f)
      close()
    }
    path(
      fill = SolidColor(outlineColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(324.889f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(327.111f)
      verticalLineTo(12.222f)
      horizontalLineTo(321.334f)
      curveTo(321.426f, 12.056f, 321.486f, 11.88f, 321.514f, 11.694f)
      curveTo(321.542f, 11.509f, 321.556f, 11.315f, 321.556f, 11.111f)
      curveTo(321.556f, 10.185f, 321.232f, 9.398f, 320.584f, 8.75f)
      curveTo(319.935f, 8.102f, 319.148f, 7.778f, 318.222f, 7.778f)
      curveTo(317.797f, 7.778f, 317.398f, 7.857f, 317.028f, 8.014f)
      curveTo(316.658f, 8.171f, 316.315f, 8.389f, 316.0f, 8.667f)
      curveTo(315.685f, 8.37f, 315.343f, 8.148f, 314.972f, 8.0f)
      curveTo(314.602f, 7.852f, 314.204f, 7.778f, 313.778f, 7.778f)
      curveTo(312.852f, 7.778f, 312.065f, 8.102f, 311.417f, 8.75f)
      curveTo(310.769f, 9.398f, 310.445f, 10.185f, 310.445f, 11.111f)
      curveTo(310.445f, 11.315f, 310.463f, 11.505f, 310.5f, 11.681f)
      curveTo(310.537f, 11.856f, 310.593f, 12.037f, 310.667f, 12.222f)
      horizontalLineTo(304.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(307.111f)
      verticalLineTo(31.111f)
      horizontalLineTo(324.889f)
      close()
      moveTo(313.778f, 10.0f)
      curveTo(314.093f, 10.0f, 314.357f, 10.106f, 314.57f, 10.319f)
      curveTo(314.783f, 10.533f, 314.889f, 10.796f, 314.889f, 11.111f)
      curveTo(314.889f, 11.426f, 314.783f, 11.69f, 314.57f, 11.903f)
      curveTo(314.357f, 12.116f, 314.093f, 12.222f, 313.778f, 12.222f)
      curveTo(313.463f, 12.222f, 313.199f, 12.116f, 312.986f, 11.903f)
      curveTo(312.773f, 11.69f, 312.667f, 11.426f, 312.667f, 11.111f)
      curveTo(312.667f, 10.796f, 312.773f, 10.533f, 312.986f, 10.319f)
      curveTo(313.199f, 10.106f, 313.463f, 10.0f, 313.778f, 10.0f)
      close()
      moveTo(319.334f, 11.111f)
      curveTo(319.334f, 11.426f, 319.227f, 11.69f, 319.014f, 11.903f)
      curveTo(318.801f, 12.116f, 318.537f, 12.222f, 318.222f, 12.222f)
      curveTo(317.908f, 12.222f, 317.644f, 12.116f, 317.431f, 11.903f)
      curveTo(317.218f, 11.69f, 317.111f, 11.426f, 317.111f, 11.111f)
      curveTo(317.111f, 10.796f, 317.218f, 10.533f, 317.431f, 10.319f)
      curveTo(317.644f, 10.106f, 317.908f, 10.0f, 318.222f, 10.0f)
      curveTo(318.537f, 10.0f, 318.801f, 10.106f, 319.014f, 10.319f)
      curveTo(319.227f, 10.533f, 319.334f, 10.796f, 319.334f, 11.111f)
      close()
      moveTo(324.889f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(317.111f)
      verticalLineTo(14.444f)
      horizontalLineTo(324.889f)
      close()
      moveTo(317.111f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(322.667f)
      verticalLineTo(28.889f)
      horizontalLineTo(317.111f)
      close()
      moveTo(314.889f, 28.889f)
      horizontalLineTo(309.334f)
      verticalLineTo(18.889f)
      horizontalLineTo(314.889f)
      verticalLineTo(28.889f)
      close()
      moveTo(307.111f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(314.889f)
      verticalLineTo(16.667f)
      horizontalLineTo(307.111f)
      close()
    }
    path(
      fill = SolidColor(backgroundColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(247.0f, 170.0f)
      verticalLineTo(149.0f)
      horizontalLineTo(242.125f)
      verticalLineTo(136.0f)
      horizontalLineTo(208.0f)
      verticalLineTo(170.0f)
      horizontalLineTo(213.85f)
      verticalLineTo(176.0f)
      horizontalLineTo(234.325f)
      verticalLineTo(170.0f)
      horizontalLineTo(247.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(236.166f, 154.889f)
      verticalLineTo(167.111f)
      horizontalLineTo(218.833f)
      verticalLineTo(154.889f)
      horizontalLineTo(216.666f)
      verticalLineTo(148.222f)
      horizontalLineTo(222.3f)
      curveTo(222.227f, 148.037f, 222.173f, 147.857f, 222.137f, 147.681f)
      curveTo(222.101f, 147.505f, 222.083f, 147.315f, 222.083f, 147.111f)
      curveTo(222.083f, 146.185f, 222.399f, 145.398f, 223.031f, 144.75f)
      curveTo(223.663f, 144.102f, 224.43f, 143.778f, 225.333f, 143.778f)
      curveTo(225.748f, 143.778f, 226.136f, 143.852f, 226.497f, 144.0f)
      curveTo(226.859f, 144.148f, 227.193f, 144.37f, 227.5f, 144.667f)
      curveTo(227.806f, 144.389f, 228.141f, 144.171f, 228.502f, 144.014f)
      curveTo(228.863f, 143.857f, 229.251f, 143.778f, 229.666f, 143.778f)
      curveTo(230.569f, 143.778f, 231.336f, 144.102f, 231.968f, 144.75f)
      curveTo(232.6f, 145.398f, 232.916f, 146.185f, 232.916f, 147.111f)
      curveTo(232.916f, 147.315f, 232.903f, 147.509f, 232.876f, 147.694f)
      curveTo(232.849f, 147.88f, 232.79f, 148.056f, 232.7f, 148.222f)
      horizontalLineTo(238.333f)
      verticalLineTo(154.889f)
      horizontalLineTo(236.166f)
      close()
    }
    path(
      fill = SolidColor(outlineColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(236.166f, 167.111f)
      verticalLineTo(154.889f)
      horizontalLineTo(238.333f)
      verticalLineTo(148.222f)
      horizontalLineTo(232.7f)
      curveTo(232.79f, 148.056f, 232.849f, 147.88f, 232.876f, 147.694f)
      curveTo(232.903f, 147.509f, 232.916f, 147.315f, 232.916f, 147.111f)
      curveTo(232.916f, 146.185f, 232.6f, 145.398f, 231.968f, 144.75f)
      curveTo(231.336f, 144.102f, 230.569f, 143.778f, 229.666f, 143.778f)
      curveTo(229.251f, 143.778f, 228.863f, 143.857f, 228.502f, 144.014f)
      curveTo(228.141f, 144.171f, 227.806f, 144.389f, 227.5f, 144.667f)
      curveTo(227.193f, 144.37f, 226.859f, 144.148f, 226.497f, 144.0f)
      curveTo(226.136f, 143.852f, 225.748f, 143.778f, 225.333f, 143.778f)
      curveTo(224.43f, 143.778f, 223.663f, 144.102f, 223.031f, 144.75f)
      curveTo(222.399f, 145.398f, 222.083f, 146.185f, 222.083f, 147.111f)
      curveTo(222.083f, 147.315f, 222.101f, 147.505f, 222.137f, 147.681f)
      curveTo(222.173f, 147.857f, 222.227f, 148.037f, 222.3f, 148.222f)
      horizontalLineTo(216.666f)
      verticalLineTo(154.889f)
      horizontalLineTo(218.833f)
      verticalLineTo(167.111f)
      horizontalLineTo(236.166f)
      close()
      moveTo(225.333f, 146.0f)
      curveTo(225.64f, 146.0f, 225.897f, 146.107f, 226.105f, 146.319f)
      curveTo(226.312f, 146.532f, 226.416f, 146.796f, 226.416f, 147.111f)
      curveTo(226.416f, 147.426f, 226.312f, 147.69f, 226.105f, 147.903f)
      curveTo(225.897f, 148.116f, 225.64f, 148.222f, 225.333f, 148.222f)
      curveTo(225.026f, 148.222f, 224.769f, 148.116f, 224.561f, 147.903f)
      curveTo(224.353f, 147.69f, 224.25f, 147.426f, 224.25f, 147.111f)
      curveTo(224.25f, 146.796f, 224.353f, 146.532f, 224.561f, 146.319f)
      curveTo(224.769f, 146.107f, 225.026f, 146.0f, 225.333f, 146.0f)
      close()
      moveTo(230.75f, 147.111f)
      curveTo(230.75f, 147.426f, 230.646f, 147.69f, 230.438f, 147.903f)
      curveTo(230.23f, 148.116f, 229.973f, 148.222f, 229.666f, 148.222f)
      curveTo(229.359f, 148.222f, 229.102f, 148.116f, 228.894f, 147.903f)
      curveTo(228.687f, 147.69f, 228.583f, 147.426f, 228.583f, 147.111f)
      curveTo(228.583f, 146.796f, 228.687f, 146.532f, 228.894f, 146.319f)
      curveTo(229.102f, 146.107f, 229.359f, 146.0f, 229.666f, 146.0f)
      curveTo(229.973f, 146.0f, 230.23f, 146.107f, 230.438f, 146.319f)
      curveTo(230.646f, 146.532f, 230.75f, 146.796f, 230.75f, 147.111f)
      close()
      moveTo(236.166f, 150.444f)
      verticalLineTo(152.667f)
      horizontalLineTo(228.583f)
      verticalLineTo(150.444f)
      horizontalLineTo(236.166f)
      close()
      moveTo(228.583f, 164.889f)
      verticalLineTo(154.889f)
      horizontalLineTo(234.0f)
      verticalLineTo(164.889f)
      horizontalLineTo(228.583f)
      close()
      moveTo(226.416f, 164.889f)
      horizontalLineTo(221.0f)
      verticalLineTo(154.889f)
      horizontalLineTo(226.416f)
      verticalLineTo(164.889f)
      close()
      moveTo(218.833f, 152.667f)
      verticalLineTo(150.444f)
      horizontalLineTo(226.416f)
      verticalLineTo(152.667f)
      horizontalLineTo(218.833f)
      close()
    }
    path(
      fill = SolidColor(themeColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(88.0f, 144.0f)
      horizontalLineToRelative(83.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-83.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(360.0f, 136.0f)
      lineToRelative(-32.0f, 0.0f)
      lineToRelative(-0.0f, 24.0f)
      lineToRelative(32.0f, 0.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(232.0f, 16.0f)
      horizontalLineToRelative(41.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-41.0f)
      close()
    }
  }
}
  .build()
