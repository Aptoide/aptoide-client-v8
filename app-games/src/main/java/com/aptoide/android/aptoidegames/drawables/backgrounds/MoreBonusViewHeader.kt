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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
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
  color4: Color = Palette.GreyDark
): ImageVector = Builder(
  name = "MoreBonusHeader",
  defaultWidth = 360.0.dp,
  defaultHeight = 56.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 56.0f
).apply {
  path(
  fill = SolidColor(color4),
  stroke = null,
  pathFillType = NonZero
) {
  moveTo(0.0f, 34.0f)
  lineTo(360.0f, 34.0f)
  lineTo(360.0f, 56.0f)
  lineTo(0.0f, 56.0f)
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
