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
private fun WantMoreViewHeaderPreview() {
  Image(
    imageVector = getWantMoreViewHeader(Color.Green, Color.Black, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getWantMoreViewHeader(
  color1: Color = Palette.Secondary,
  color2: Color = Palette.Black,
  color3: Color = Palette.Primary,
  color4: Color = Palette.Black,
): ImageVector = ImageVector.Builder(
  name = "WantMoreHeader",
  defaultWidth = 360.0.dp,
  defaultHeight = 32.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 32.0f
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
    moveTo(0.0f, 16.0f)
    horizontalLineToRelative(360.0f)
    verticalLineToRelative(16.0f)
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
    moveTo(336.0f, 27.2f)
    verticalLineTo(10.4f)
    horizontalLineTo(331.0f)
    verticalLineTo(0.0f)
    horizontalLineTo(296.0f)
    verticalLineTo(27.2f)
    horizontalLineTo(302.0f)
    verticalLineTo(32.0f)
    horizontalLineTo(323.0f)
    verticalLineTo(27.2f)
    horizontalLineTo(336.0f)
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
      moveTo(324.887f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(307.109f)
      verticalLineTo(18.889f)
      horizontalLineTo(304.887f)
      verticalLineTo(12.222f)
      horizontalLineTo(310.665f)
      curveTo(310.591f, 12.037f, 310.535f, 11.856f, 310.498f, 11.681f)
      curveTo(310.461f, 11.505f, 310.443f, 11.315f, 310.443f, 11.111f)
      curveTo(310.443f, 10.185f, 310.767f, 9.398f, 311.415f, 8.75f)
      curveTo(312.063f, 8.102f, 312.85f, 7.778f, 313.776f, 7.778f)
      curveTo(314.202f, 7.778f, 314.6f, 7.852f, 314.97f, 8.0f)
      curveTo(315.341f, 8.148f, 315.683f, 8.37f, 315.998f, 8.667f)
      curveTo(316.313f, 8.389f, 316.656f, 8.171f, 317.026f, 8.014f)
      curveTo(317.396f, 7.857f, 317.795f, 7.778f, 318.22f, 7.778f)
      curveTo(319.146f, 7.778f, 319.933f, 8.102f, 320.582f, 8.75f)
      curveTo(321.23f, 9.398f, 321.554f, 10.185f, 321.554f, 11.111f)
      curveTo(321.554f, 11.315f, 321.54f, 11.509f, 321.512f, 11.694f)
      curveTo(321.484f, 11.88f, 321.424f, 12.056f, 321.332f, 12.222f)
      horizontalLineTo(327.109f)
      verticalLineTo(18.889f)
      horizontalLineTo(324.887f)
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
      moveTo(324.887f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(327.109f)
      verticalLineTo(12.222f)
      horizontalLineTo(321.332f)
      curveTo(321.424f, 12.056f, 321.484f, 11.88f, 321.512f, 11.694f)
      curveTo(321.54f, 11.509f, 321.554f, 11.315f, 321.554f, 11.111f)
      curveTo(321.554f, 10.185f, 321.23f, 9.398f, 320.582f, 8.75f)
      curveTo(319.933f, 8.102f, 319.146f, 7.778f, 318.22f, 7.778f)
      curveTo(317.795f, 7.778f, 317.396f, 7.857f, 317.026f, 8.014f)
      curveTo(316.656f, 8.171f, 316.313f, 8.389f, 315.998f, 8.667f)
      curveTo(315.683f, 8.37f, 315.341f, 8.148f, 314.97f, 8.0f)
      curveTo(314.6f, 7.852f, 314.202f, 7.778f, 313.776f, 7.778f)
      curveTo(312.85f, 7.778f, 312.063f, 8.102f, 311.415f, 8.75f)
      curveTo(310.767f, 9.398f, 310.443f, 10.185f, 310.443f, 11.111f)
      curveTo(310.443f, 11.315f, 310.461f, 11.505f, 310.498f, 11.681f)
      curveTo(310.535f, 11.856f, 310.591f, 12.037f, 310.665f, 12.222f)
      horizontalLineTo(304.887f)
      verticalLineTo(18.889f)
      horizontalLineTo(307.109f)
      verticalLineTo(31.111f)
      horizontalLineTo(324.887f)
      close()
      moveTo(313.776f, 10.0f)
      curveTo(314.091f, 10.0f, 314.355f, 10.106f, 314.568f, 10.319f)
      curveTo(314.781f, 10.533f, 314.887f, 10.796f, 314.887f, 11.111f)
      curveTo(314.887f, 11.426f, 314.781f, 11.69f, 314.568f, 11.903f)
      curveTo(314.355f, 12.116f, 314.091f, 12.222f, 313.776f, 12.222f)
      curveTo(313.461f, 12.222f, 313.197f, 12.116f, 312.984f, 11.903f)
      curveTo(312.771f, 11.69f, 312.665f, 11.426f, 312.665f, 11.111f)
      curveTo(312.665f, 10.796f, 312.771f, 10.533f, 312.984f, 10.319f)
      curveTo(313.197f, 10.106f, 313.461f, 10.0f, 313.776f, 10.0f)
      close()
      moveTo(319.332f, 11.111f)
      curveTo(319.332f, 11.426f, 319.225f, 11.69f, 319.012f, 11.903f)
      curveTo(318.799f, 12.116f, 318.535f, 12.222f, 318.22f, 12.222f)
      curveTo(317.906f, 12.222f, 317.642f, 12.116f, 317.429f, 11.903f)
      curveTo(317.216f, 11.69f, 317.109f, 11.426f, 317.109f, 11.111f)
      curveTo(317.109f, 10.796f, 317.216f, 10.533f, 317.429f, 10.319f)
      curveTo(317.642f, 10.106f, 317.906f, 10.0f, 318.22f, 10.0f)
      curveTo(318.535f, 10.0f, 318.799f, 10.106f, 319.012f, 10.319f)
      curveTo(319.225f, 10.533f, 319.332f, 10.796f, 319.332f, 11.111f)
      close()
      moveTo(324.887f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(317.109f)
      verticalLineTo(14.444f)
      horizontalLineTo(324.887f)
      close()
      moveTo(317.109f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(322.665f)
      verticalLineTo(28.889f)
      horizontalLineTo(317.109f)
      close()
      moveTo(314.887f, 28.889f)
      horizontalLineTo(309.332f)
      verticalLineTo(18.889f)
      horizontalLineTo(314.887f)
      verticalLineTo(28.889f)
      close()
      moveTo(307.109f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(314.887f)
      verticalLineTo(16.667f)
      horizontalLineTo(307.109f)
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
      moveTo(232.0f, 16.0f)
      horizontalLineToRelative(41.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-41.0f)
      close()
    }
  }
}.build()
