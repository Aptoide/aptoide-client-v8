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
  color4: Color = Palette.GreyDark,
): ImageVector = Builder(
  name = "Bannerbonus",
  defaultWidth = 262.0.dp,
  defaultHeight = 41.0.dp,
  viewportWidth = 262.0f,
  viewportHeight = 41.0f
).apply {
  path(
    fill = SolidColor(color4),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0f, 20.5f)
    lineTo(262f, 20.5f)
    lineTo(262f, 41f)
    lineTo(0f, 41f)
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
    moveTo(40.0f, 10.118f)
    horizontalLineToRelative(222.0f)
    verticalLineToRelative(24.282f)
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
    moveTo(40.0f, 34.4f)
    verticalLineTo(13.153f)
    horizontalLineTo(35.0f)
    verticalLineTo(0.0f)
    horizontalLineTo(0.0f)
    verticalLineTo(34.4f)
    horizontalLineTo(6.0f)
    verticalLineTo(40.471f)
    horizontalLineTo(27.0f)
    verticalLineTo(34.4f)
    horizontalLineTo(40.0f)
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
      moveTo(28.889f, 19.111f)
      verticalLineTo(31.477f)
      horizontalLineTo(11.111f)
      verticalLineTo(19.111f)
      horizontalLineTo(8.889f)
      verticalLineTo(12.366f)
      horizontalLineTo(14.667f)
      curveTo(14.593f, 12.179f, 14.537f, 11.996f, 14.5f, 11.818f)
      curveTo(14.463f, 11.64f, 14.445f, 11.448f, 14.445f, 11.242f)
      curveTo(14.445f, 10.305f, 14.769f, 9.509f, 15.417f, 8.853f)
      curveTo(16.065f, 8.197f, 16.852f, 7.869f, 17.778f, 7.869f)
      curveTo(18.204f, 7.869f, 18.602f, 7.944f, 18.972f, 8.094f)
      curveTo(19.343f, 8.244f, 19.685f, 8.469f, 20.0f, 8.769f)
      curveTo(20.315f, 8.488f, 20.658f, 8.267f, 21.028f, 8.108f)
      curveTo(21.398f, 7.949f, 21.796f, 7.869f, 22.222f, 7.869f)
      curveTo(23.148f, 7.869f, 23.935f, 8.197f, 24.584f, 8.853f)
      curveTo(25.232f, 9.509f, 25.556f, 10.305f, 25.556f, 11.242f)
      curveTo(25.556f, 11.448f, 25.542f, 11.645f, 25.514f, 11.832f)
      curveTo(25.486f, 12.019f, 25.426f, 12.197f, 25.333f, 12.366f)
      horizontalLineTo(31.111f)
      verticalLineTo(19.111f)
      horizontalLineTo(28.889f)
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
      moveTo(28.889f, 31.477f)
      verticalLineTo(19.111f)
      horizontalLineTo(31.111f)
      verticalLineTo(12.366f)
      horizontalLineTo(25.333f)
      curveTo(25.426f, 12.197f, 25.486f, 12.019f, 25.514f, 11.832f)
      curveTo(25.542f, 11.645f, 25.556f, 11.448f, 25.556f, 11.242f)
      curveTo(25.556f, 10.305f, 25.232f, 9.509f, 24.584f, 8.853f)
      curveTo(23.935f, 8.197f, 23.148f, 7.869f, 22.222f, 7.869f)
      curveTo(21.796f, 7.869f, 21.398f, 7.949f, 21.028f, 8.108f)
      curveTo(20.658f, 8.267f, 20.315f, 8.488f, 20.0f, 8.769f)
      curveTo(19.685f, 8.469f, 19.343f, 8.244f, 18.972f, 8.094f)
      curveTo(18.602f, 7.944f, 18.204f, 7.869f, 17.778f, 7.869f)
      curveTo(16.852f, 7.869f, 16.065f, 8.197f, 15.417f, 8.853f)
      curveTo(14.769f, 9.509f, 14.445f, 10.305f, 14.445f, 11.242f)
      curveTo(14.445f, 11.448f, 14.463f, 11.64f, 14.5f, 11.818f)
      curveTo(14.537f, 11.996f, 14.593f, 12.179f, 14.667f, 12.366f)
      horizontalLineTo(8.889f)
      verticalLineTo(19.111f)
      horizontalLineTo(11.111f)
      verticalLineTo(31.477f)
      horizontalLineTo(28.889f)
      close()
      moveTo(17.778f, 10.118f)
      curveTo(18.093f, 10.118f, 18.357f, 10.225f, 18.57f, 10.441f)
      curveTo(18.783f, 10.656f, 18.889f, 10.923f, 18.889f, 11.242f)
      curveTo(18.889f, 11.56f, 18.783f, 11.827f, 18.57f, 12.043f)
      curveTo(18.357f, 12.258f, 18.093f, 12.366f, 17.778f, 12.366f)
      curveTo(17.463f, 12.366f, 17.199f, 12.258f, 16.986f, 12.043f)
      curveTo(16.773f, 11.827f, 16.667f, 11.56f, 16.667f, 11.242f)
      curveTo(16.667f, 10.923f, 16.773f, 10.656f, 16.986f, 10.441f)
      curveTo(17.199f, 10.225f, 17.463f, 10.118f, 17.778f, 10.118f)
      close()
      moveTo(23.334f, 11.242f)
      curveTo(23.334f, 11.56f, 23.227f, 11.827f, 23.014f, 12.043f)
      curveTo(22.801f, 12.258f, 22.537f, 12.366f, 22.222f, 12.366f)
      curveTo(21.908f, 12.366f, 21.644f, 12.258f, 21.431f, 12.043f)
      curveTo(21.218f, 11.827f, 21.111f, 11.56f, 21.111f, 11.242f)
      curveTo(21.111f, 10.923f, 21.218f, 10.656f, 21.431f, 10.441f)
      curveTo(21.644f, 10.225f, 21.908f, 10.118f, 22.222f, 10.118f)
      curveTo(22.537f, 10.118f, 22.801f, 10.225f, 23.014f, 10.441f)
      curveTo(23.227f, 10.656f, 23.334f, 10.923f, 23.334f, 11.242f)
      close()
      moveTo(28.889f, 14.614f)
      verticalLineTo(16.863f)
      horizontalLineTo(21.111f)
      verticalLineTo(14.614f)
      horizontalLineTo(28.889f)
      close()
      moveTo(21.111f, 29.229f)
      verticalLineTo(19.111f)
      horizontalLineTo(26.667f)
      verticalLineTo(29.229f)
      horizontalLineTo(21.111f)
      close()
      moveTo(18.889f, 29.229f)
      horizontalLineTo(13.334f)
      verticalLineTo(19.111f)
      horizontalLineTo(18.889f)
      verticalLineTo(29.229f)
      close()
      moveTo(11.111f, 16.863f)
      verticalLineTo(14.614f)
      horizontalLineTo(18.889f)
      verticalLineTo(16.863f)
      horizontalLineTo(11.111f)
      close()
    }
  }
}.build()
