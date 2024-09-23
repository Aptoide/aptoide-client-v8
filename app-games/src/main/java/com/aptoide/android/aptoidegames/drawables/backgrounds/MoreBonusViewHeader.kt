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
private fun MoreBonusViewHeaderPreview() {
  Image(
    imageVector = getMoreBonusViewHeader(Color.Green, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getMoreBonusViewHeader(
  color1: Color = Palette.Secondary,
  color2: Color = Palette.Primary,
  color3: Color = Palette.Black,
): ImageVector = ImageVector.Builder(
  name = "MoreBonusHeader",
  defaultWidth = 360.0.dp,
  defaultHeight = 56.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 56.0f
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
    moveTo(0.0f, 40.0f)
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
    moveTo(352.0f, 50.0f)
    verticalLineTo(29.0f)
    horizontalLineTo(347.0f)
    verticalLineTo(16.0f)
    horizontalLineTo(312.0f)
    verticalLineTo(50.0f)
    horizontalLineTo(318.0f)
    verticalLineTo(56.0f)
    horizontalLineTo(339.0f)
    verticalLineTo(50.0f)
    horizontalLineTo(352.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(color2),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(340.887f, 34.889f)
      verticalLineTo(47.111f)
      horizontalLineTo(323.109f)
      verticalLineTo(34.889f)
      horizontalLineTo(320.887f)
      verticalLineTo(28.222f)
      horizontalLineTo(326.665f)
      curveTo(326.591f, 28.037f, 326.535f, 27.857f, 326.498f, 27.681f)
      curveTo(326.461f, 27.505f, 326.443f, 27.315f, 326.443f, 27.111f)
      curveTo(326.443f, 26.185f, 326.767f, 25.398f, 327.415f, 24.75f)
      curveTo(328.063f, 24.102f, 328.85f, 23.778f, 329.776f, 23.778f)
      curveTo(330.202f, 23.778f, 330.6f, 23.852f, 330.97f, 24.0f)
      curveTo(331.341f, 24.148f, 331.683f, 24.37f, 331.998f, 24.667f)
      curveTo(332.313f, 24.389f, 332.656f, 24.171f, 333.026f, 24.014f)
      curveTo(333.396f, 23.857f, 333.795f, 23.778f, 334.22f, 23.778f)
      curveTo(335.146f, 23.778f, 335.933f, 24.102f, 336.582f, 24.75f)
      curveTo(337.23f, 25.398f, 337.554f, 26.185f, 337.554f, 27.111f)
      curveTo(337.554f, 27.315f, 337.54f, 27.509f, 337.512f, 27.694f)
      curveTo(337.484f, 27.88f, 337.424f, 28.056f, 337.332f, 28.222f)
      horizontalLineTo(343.109f)
      verticalLineTo(34.889f)
      horizontalLineTo(340.887f)
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
      moveTo(340.887f, 47.111f)
      verticalLineTo(34.889f)
      horizontalLineTo(343.109f)
      verticalLineTo(28.222f)
      horizontalLineTo(337.332f)
      curveTo(337.424f, 28.056f, 337.484f, 27.88f, 337.512f, 27.694f)
      curveTo(337.54f, 27.509f, 337.554f, 27.315f, 337.554f, 27.111f)
      curveTo(337.554f, 26.185f, 337.23f, 25.398f, 336.582f, 24.75f)
      curveTo(335.933f, 24.102f, 335.146f, 23.778f, 334.22f, 23.778f)
      curveTo(333.795f, 23.778f, 333.396f, 23.857f, 333.026f, 24.014f)
      curveTo(332.656f, 24.171f, 332.313f, 24.389f, 331.998f, 24.667f)
      curveTo(331.683f, 24.37f, 331.341f, 24.148f, 330.97f, 24.0f)
      curveTo(330.6f, 23.852f, 330.202f, 23.778f, 329.776f, 23.778f)
      curveTo(328.85f, 23.778f, 328.063f, 24.102f, 327.415f, 24.75f)
      curveTo(326.767f, 25.398f, 326.443f, 26.185f, 326.443f, 27.111f)
      curveTo(326.443f, 27.315f, 326.461f, 27.505f, 326.498f, 27.681f)
      curveTo(326.535f, 27.857f, 326.591f, 28.037f, 326.665f, 28.222f)
      horizontalLineTo(320.887f)
      verticalLineTo(34.889f)
      horizontalLineTo(323.109f)
      verticalLineTo(47.111f)
      horizontalLineTo(340.887f)
      close()
      moveTo(329.776f, 26.0f)
      curveTo(330.091f, 26.0f, 330.355f, 26.107f, 330.568f, 26.319f)
      curveTo(330.781f, 26.532f, 330.887f, 26.796f, 330.887f, 27.111f)
      curveTo(330.887f, 27.426f, 330.781f, 27.69f, 330.568f, 27.903f)
      curveTo(330.355f, 28.116f, 330.091f, 28.222f, 329.776f, 28.222f)
      curveTo(329.461f, 28.222f, 329.197f, 28.116f, 328.984f, 27.903f)
      curveTo(328.771f, 27.69f, 328.665f, 27.426f, 328.665f, 27.111f)
      curveTo(328.665f, 26.796f, 328.771f, 26.532f, 328.984f, 26.319f)
      curveTo(329.197f, 26.107f, 329.461f, 26.0f, 329.776f, 26.0f)
      close()
      moveTo(335.332f, 27.111f)
      curveTo(335.332f, 27.426f, 335.225f, 27.69f, 335.012f, 27.903f)
      curveTo(334.799f, 28.116f, 334.535f, 28.222f, 334.22f, 28.222f)
      curveTo(333.906f, 28.222f, 333.642f, 28.116f, 333.429f, 27.903f)
      curveTo(333.216f, 27.69f, 333.109f, 27.426f, 333.109f, 27.111f)
      curveTo(333.109f, 26.796f, 333.216f, 26.532f, 333.429f, 26.319f)
      curveTo(333.642f, 26.107f, 333.906f, 26.0f, 334.22f, 26.0f)
      curveTo(334.535f, 26.0f, 334.799f, 26.107f, 335.012f, 26.319f)
      curveTo(335.225f, 26.532f, 335.332f, 26.796f, 335.332f, 27.111f)
      close()
      moveTo(340.887f, 30.444f)
      verticalLineTo(32.667f)
      horizontalLineTo(333.109f)
      verticalLineTo(30.444f)
      horizontalLineTo(340.887f)
      close()
      moveTo(333.109f, 44.889f)
      verticalLineTo(34.889f)
      horizontalLineTo(338.665f)
      verticalLineTo(44.889f)
      horizontalLineTo(333.109f)
      close()
      moveTo(330.887f, 44.889f)
      horizontalLineTo(325.332f)
      verticalLineTo(34.889f)
      horizontalLineTo(330.887f)
      verticalLineTo(44.889f)
      close()
      moveTo(323.109f, 32.667f)
      verticalLineTo(30.444f)
      horizontalLineTo(330.887f)
      verticalLineTo(32.667f)
      horizontalLineTo(323.109f)
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
      moveTo(40.0f, 10.0f)
      horizontalLineToRelative(222.0f)
      verticalLineToRelative(24.0f)
      horizontalLineToRelative(-222.0f)
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
      moveTo(40.0f, 34.0f)
      verticalLineTo(13.0f)
      horizontalLineTo(35.0f)
      verticalLineTo(0.0f)
      horizontalLineTo(0.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(6.0f)
      verticalLineTo(40.0f)
      horizontalLineTo(27.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(40.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(color2),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(28.887f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(11.109f)
      verticalLineTo(18.889f)
      horizontalLineTo(8.887f)
      verticalLineTo(12.222f)
      horizontalLineTo(14.665f)
      curveTo(14.591f, 12.037f, 14.535f, 11.856f, 14.498f, 11.681f)
      curveTo(14.461f, 11.505f, 14.443f, 11.315f, 14.443f, 11.111f)
      curveTo(14.443f, 10.185f, 14.767f, 9.398f, 15.415f, 8.75f)
      curveTo(16.063f, 8.102f, 16.85f, 7.778f, 17.776f, 7.778f)
      curveTo(18.202f, 7.778f, 18.6f, 7.852f, 18.971f, 8.0f)
      curveTo(19.341f, 8.148f, 19.683f, 8.37f, 19.998f, 8.667f)
      curveTo(20.313f, 8.389f, 20.656f, 8.171f, 21.026f, 8.014f)
      curveTo(21.396f, 7.857f, 21.795f, 7.778f, 22.221f, 7.778f)
      curveTo(23.146f, 7.778f, 23.933f, 8.102f, 24.582f, 8.75f)
      curveTo(25.23f, 9.398f, 25.554f, 10.185f, 25.554f, 11.111f)
      curveTo(25.554f, 11.315f, 25.54f, 11.509f, 25.512f, 11.694f)
      curveTo(25.484f, 11.88f, 25.424f, 12.056f, 25.332f, 12.222f)
      horizontalLineTo(31.109f)
      verticalLineTo(18.889f)
      horizontalLineTo(28.887f)
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
      moveTo(28.887f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(31.109f)
      verticalLineTo(12.222f)
      horizontalLineTo(25.332f)
      curveTo(25.424f, 12.056f, 25.484f, 11.88f, 25.512f, 11.694f)
      curveTo(25.54f, 11.509f, 25.554f, 11.315f, 25.554f, 11.111f)
      curveTo(25.554f, 10.185f, 25.23f, 9.398f, 24.582f, 8.75f)
      curveTo(23.933f, 8.102f, 23.146f, 7.778f, 22.221f, 7.778f)
      curveTo(21.795f, 7.778f, 21.396f, 7.857f, 21.026f, 8.014f)
      curveTo(20.656f, 8.171f, 20.313f, 8.389f, 19.998f, 8.667f)
      curveTo(19.683f, 8.37f, 19.341f, 8.148f, 18.971f, 8.0f)
      curveTo(18.6f, 7.852f, 18.202f, 7.778f, 17.776f, 7.778f)
      curveTo(16.85f, 7.778f, 16.063f, 8.102f, 15.415f, 8.75f)
      curveTo(14.767f, 9.398f, 14.443f, 10.185f, 14.443f, 11.111f)
      curveTo(14.443f, 11.315f, 14.461f, 11.505f, 14.498f, 11.681f)
      curveTo(14.535f, 11.856f, 14.591f, 12.037f, 14.665f, 12.222f)
      horizontalLineTo(8.887f)
      verticalLineTo(18.889f)
      horizontalLineTo(11.109f)
      verticalLineTo(31.111f)
      horizontalLineTo(28.887f)
      close()
      moveTo(17.776f, 10.0f)
      curveTo(18.091f, 10.0f, 18.355f, 10.106f, 18.568f, 10.319f)
      curveTo(18.781f, 10.533f, 18.887f, 10.796f, 18.887f, 11.111f)
      curveTo(18.887f, 11.426f, 18.781f, 11.69f, 18.568f, 11.903f)
      curveTo(18.355f, 12.116f, 18.091f, 12.222f, 17.776f, 12.222f)
      curveTo(17.461f, 12.222f, 17.197f, 12.116f, 16.984f, 11.903f)
      curveTo(16.771f, 11.69f, 16.665f, 11.426f, 16.665f, 11.111f)
      curveTo(16.665f, 10.796f, 16.771f, 10.533f, 16.984f, 10.319f)
      curveTo(17.197f, 10.106f, 17.461f, 10.0f, 17.776f, 10.0f)
      close()
      moveTo(23.332f, 11.111f)
      curveTo(23.332f, 11.426f, 23.225f, 11.69f, 23.012f, 11.903f)
      curveTo(22.799f, 12.116f, 22.535f, 12.222f, 22.221f, 12.222f)
      curveTo(21.906f, 12.222f, 21.642f, 12.116f, 21.429f, 11.903f)
      curveTo(21.216f, 11.69f, 21.109f, 11.426f, 21.109f, 11.111f)
      curveTo(21.109f, 10.796f, 21.216f, 10.533f, 21.429f, 10.319f)
      curveTo(21.642f, 10.106f, 21.906f, 10.0f, 22.221f, 10.0f)
      curveTo(22.535f, 10.0f, 22.799f, 10.106f, 23.012f, 10.319f)
      curveTo(23.225f, 10.533f, 23.332f, 10.796f, 23.332f, 11.111f)
      close()
      moveTo(28.887f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(21.109f)
      verticalLineTo(14.444f)
      horizontalLineTo(28.887f)
      close()
      moveTo(21.109f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(26.665f)
      verticalLineTo(28.889f)
      horizontalLineTo(21.109f)
      close()
      moveTo(18.887f, 28.889f)
      horizontalLineTo(13.332f)
      verticalLineTo(18.889f)
      horizontalLineTo(18.887f)
      verticalLineTo(28.889f)
      close()
      moveTo(11.109f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(18.887f)
      verticalLineTo(16.667f)
      horizontalLineTo(11.109f)
      close()
    }
  }
}.build()
