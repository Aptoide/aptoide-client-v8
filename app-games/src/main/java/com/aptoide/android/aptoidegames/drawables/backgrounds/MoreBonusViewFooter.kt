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
private fun MoreBonusViewPreview() {
  Image(
    imageVector = getMoreBonusViewFooter(Color.Green, Color.Black, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getMoreBonusViewFooter(
  color1: Color = Palette.Secondary,
  color2: Color = Palette.Black,
  color3: Color = Palette.Primary,
  color4: Color = Palette.Black,
): ImageVector = ImageVector.Builder(
  name = "MorebonusFooter",
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
    verticalLineToRelative(19.0f)
    horizontalLineToRelative(-360.0f)
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
    moveTo(120.0f, 8.0f)
    horizontalLineToRelative(83.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-83.0f)
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
    moveTo(319.0f, 34.0f)
    verticalLineTo(13.0f)
    horizontalLineTo(314.125f)
    verticalLineTo(0.0f)
    horizontalLineTo(280.0f)
    verticalLineTo(34.0f)
    horizontalLineTo(285.85f)
    verticalLineTo(40.0f)
    horizontalLineTo(306.325f)
    verticalLineTo(34.0f)
    horizontalLineTo(319.0f)
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
      moveTo(308.165f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(290.832f)
      verticalLineTo(18.889f)
      horizontalLineTo(288.665f)
      verticalLineTo(12.222f)
      horizontalLineTo(294.299f)
      curveTo(294.226f, 12.037f, 294.172f, 11.856f, 294.136f, 11.681f)
      curveTo(294.1f, 11.505f, 294.082f, 11.315f, 294.082f, 11.111f)
      curveTo(294.082f, 10.185f, 294.398f, 9.398f, 295.03f, 8.75f)
      curveTo(295.662f, 8.102f, 296.429f, 7.778f, 297.332f, 7.778f)
      curveTo(297.747f, 7.778f, 298.136f, 7.852f, 298.497f, 8.0f)
      curveTo(298.858f, 8.148f, 299.192f, 8.37f, 299.499f, 8.667f)
      curveTo(299.806f, 8.389f, 300.14f, 8.171f, 300.501f, 8.014f)
      curveTo(300.862f, 7.857f, 301.25f, 7.778f, 301.665f, 7.778f)
      curveTo(302.568f, 7.778f, 303.336f, 8.102f, 303.967f, 8.75f)
      curveTo(304.599f, 9.398f, 304.915f, 10.185f, 304.915f, 11.111f)
      curveTo(304.915f, 11.315f, 304.902f, 11.509f, 304.875f, 11.694f)
      curveTo(304.848f, 11.88f, 304.789f, 12.056f, 304.699f, 12.222f)
      horizontalLineTo(310.332f)
      verticalLineTo(18.889f)
      horizontalLineTo(308.165f)
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
      moveTo(308.165f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(310.332f)
      verticalLineTo(12.222f)
      horizontalLineTo(304.699f)
      curveTo(304.789f, 12.056f, 304.848f, 11.88f, 304.875f, 11.694f)
      curveTo(304.902f, 11.509f, 304.915f, 11.315f, 304.915f, 11.111f)
      curveTo(304.915f, 10.185f, 304.599f, 9.398f, 303.967f, 8.75f)
      curveTo(303.336f, 8.102f, 302.568f, 7.778f, 301.665f, 7.778f)
      curveTo(301.25f, 7.778f, 300.862f, 7.857f, 300.501f, 8.014f)
      curveTo(300.14f, 8.171f, 299.806f, 8.389f, 299.499f, 8.667f)
      curveTo(299.192f, 8.37f, 298.858f, 8.148f, 298.497f, 8.0f)
      curveTo(298.136f, 7.852f, 297.747f, 7.778f, 297.332f, 7.778f)
      curveTo(296.429f, 7.778f, 295.662f, 8.102f, 295.03f, 8.75f)
      curveTo(294.398f, 9.398f, 294.082f, 10.185f, 294.082f, 11.111f)
      curveTo(294.082f, 11.315f, 294.1f, 11.505f, 294.136f, 11.681f)
      curveTo(294.172f, 11.856f, 294.226f, 12.037f, 294.299f, 12.222f)
      horizontalLineTo(288.665f)
      verticalLineTo(18.889f)
      horizontalLineTo(290.832f)
      verticalLineTo(31.111f)
      horizontalLineTo(308.165f)
      close()
      moveTo(297.332f, 10.0f)
      curveTo(297.639f, 10.0f, 297.896f, 10.106f, 298.104f, 10.319f)
      curveTo(298.312f, 10.533f, 298.415f, 10.796f, 298.415f, 11.111f)
      curveTo(298.415f, 11.426f, 298.312f, 11.69f, 298.104f, 11.903f)
      curveTo(297.896f, 12.116f, 297.639f, 12.222f, 297.332f, 12.222f)
      curveTo(297.025f, 12.222f, 296.768f, 12.116f, 296.56f, 11.903f)
      curveTo(296.353f, 11.69f, 296.249f, 11.426f, 296.249f, 11.111f)
      curveTo(296.249f, 10.796f, 296.353f, 10.533f, 296.56f, 10.319f)
      curveTo(296.768f, 10.106f, 297.025f, 10.0f, 297.332f, 10.0f)
      close()
      moveTo(302.749f, 11.111f)
      curveTo(302.749f, 11.426f, 302.645f, 11.69f, 302.437f, 11.903f)
      curveTo(302.23f, 12.116f, 301.972f, 12.222f, 301.665f, 12.222f)
      curveTo(301.358f, 12.222f, 301.101f, 12.116f, 300.893f, 11.903f)
      curveTo(300.686f, 11.69f, 300.582f, 11.426f, 300.582f, 11.111f)
      curveTo(300.582f, 10.796f, 300.686f, 10.533f, 300.893f, 10.319f)
      curveTo(301.101f, 10.106f, 301.358f, 10.0f, 301.665f, 10.0f)
      curveTo(301.972f, 10.0f, 302.23f, 10.106f, 302.437f, 10.319f)
      curveTo(302.645f, 10.533f, 302.749f, 10.796f, 302.749f, 11.111f)
      close()
      moveTo(308.165f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(300.582f)
      verticalLineTo(14.444f)
      horizontalLineTo(308.165f)
      close()
      moveTo(300.582f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(305.999f)
      verticalLineTo(28.889f)
      horizontalLineTo(300.582f)
      close()
      moveTo(298.415f, 28.889f)
      horizontalLineTo(292.999f)
      verticalLineTo(18.889f)
      horizontalLineTo(298.415f)
      verticalLineTo(28.889f)
      close()
      moveTo(290.832f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(298.415f)
      verticalLineTo(16.667f)
      horizontalLineTo(290.832f)
      close()
    }
  }
}.build()
