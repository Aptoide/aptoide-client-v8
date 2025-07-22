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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestGiftIcon() {
  Image(
    imageVector = getGiftIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGiftIcon(): ImageVector = ImageVector.Builder(
  name = "Gift",
  defaultWidth = 22.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 22.0f,
  viewportHeight = 24.0f
).apply {
  group {
    path(
      fill = SolidColor(Color(0xFF1E1E26)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(16.275f, 6.17f)
      horizontalLineTo(20.801f)
      curveTo(21.324f, 6.17f, 21.989f, 6.869f, 21.963f, 7.396f)
      curveTo(21.871f, 8.754f, 22.081f, 10.255f, 21.963f, 11.595f)
      curveTo(21.904f, 12.267f, 21.532f, 12.595f, 20.913f, 12.806f)
      lineTo(20.917f, 22.889f)
      curveTo(20.878f, 23.468f, 20.414f, 23.891f, 19.846f, 23.982f)
      curveTo(15.812f, 23.991f, 11.777f, 23.969f, 7.743f, 23.979f)
      curveTo(6.269f, 23.983f, 4.811f, 24.024f, 3.334f, 23.979f)
      curveTo(2.464f, 23.953f, 1.45f, 24.213f, 1.111f, 23.17f)
      lineTo(1.081f, 12.888f)
      curveTo(1.055f, 12.757f, 0.689f, 12.693f, 0.548f, 12.607f)
      curveTo(0.199f, 12.393f, 0.059f, 11.981f, 0.032f, 11.596f)
      curveTo(-0.066f, 10.2f, 0.1f, 8.684f, 0.045f, 7.275f)
      curveTo(0.031f, 6.876f, 0.63f, 6.215f, 1.012f, 6.215f)
      horizontalLineTo(5.493f)
      curveTo(3.438f, 4.162f, 4.625f, 0.607f, 7.493f, 0.075f)
      curveTo(8.869f, -0.18f, 10.094f, 0.217f, 11.073f, 1.167f)
      curveTo(11.592f, 0.75f, 12.146f, 0.392f, 12.816f, 0.254f)
      curveTo(14.234f, -0.039f, 15.488f, 0.38f, 16.435f, 1.438f)
      curveTo(17.706f, 2.859f, 17.563f, 4.815f, 16.275f, 6.17f)
      lineTo(16.275f, 6.17f)
      close()
      moveTo(8.029f, 2.432f)
      curveTo(7.521f, 2.505f, 7.055f, 2.927f, 6.942f, 3.418f)
      curveTo(6.672f, 4.582f, 8.079f, 5.385f, 9.056f, 4.734f)
      curveTo(10.261f, 3.933f, 9.467f, 2.226f, 8.029f, 2.432f)
      verticalLineTo(2.432f)
      close()
      moveTo(13.535f, 2.477f)
      curveTo(11.789f, 2.634f, 12.072f, 5.142f, 13.837f, 4.916f)
      curveTo(15.489f, 4.705f, 15.194f, 2.329f, 13.535f, 2.477f)
      close()
      moveTo(7.949f, 8.626f)
      horizontalLineTo(2.558f)
      lineTo(2.49f, 8.693f)
      verticalLineTo(10.366f)
      horizontalLineTo(7.949f)
      verticalLineTo(8.626f)
      close()
      moveTo(19.459f, 8.626f)
      horizontalLineTo(14.0f)
      verticalLineTo(10.366f)
      horizontalLineTo(19.459f)
      verticalLineTo(8.626f)
      close()
      moveTo(9.587f, 12.822f)
      horizontalLineTo(3.537f)
      verticalLineTo(21.571f)
      horizontalLineTo(9.587f)
      verticalLineTo(12.822f)
      close()
      moveTo(18.458f, 12.822f)
      horizontalLineTo(12.362f)
      verticalLineTo(21.571f)
      horizontalLineTo(18.458f)
      verticalLineTo(12.822f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.2f,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(16.275f, 6.17f)
      horizontalLineTo(20.801f)
      curveTo(21.324f, 6.17f, 21.989f, 6.869f, 21.963f, 7.396f)
      curveTo(21.871f, 8.754f, 22.081f, 10.255f, 21.963f, 11.595f)
      curveTo(21.904f, 12.267f, 21.532f, 12.595f, 20.913f, 12.806f)
      lineTo(20.917f, 22.889f)
      curveTo(20.878f, 23.468f, 20.414f, 23.891f, 19.846f, 23.982f)
      curveTo(15.812f, 23.991f, 11.777f, 23.969f, 7.743f, 23.979f)
      curveTo(6.269f, 23.983f, 4.811f, 24.024f, 3.334f, 23.979f)
      curveTo(2.464f, 23.953f, 1.45f, 24.213f, 1.111f, 23.17f)
      lineTo(1.081f, 12.888f)
      curveTo(1.055f, 12.757f, 0.689f, 12.693f, 0.548f, 12.607f)
      curveTo(0.199f, 12.393f, 0.059f, 11.981f, 0.032f, 11.596f)
      curveTo(-0.066f, 10.2f, 0.1f, 8.684f, 0.045f, 7.275f)
      curveTo(0.031f, 6.876f, 0.63f, 6.215f, 1.012f, 6.215f)
      horizontalLineTo(5.493f)
      curveTo(3.438f, 4.162f, 4.625f, 0.607f, 7.493f, 0.075f)
      curveTo(8.869f, -0.18f, 10.094f, 0.217f, 11.073f, 1.167f)
      curveTo(11.592f, 0.75f, 12.146f, 0.392f, 12.816f, 0.254f)
      curveTo(14.234f, -0.039f, 15.488f, 0.38f, 16.435f, 1.438f)
      curveTo(17.706f, 2.859f, 17.563f, 4.815f, 16.275f, 6.17f)
      lineTo(16.275f, 6.17f)
      close()
      moveTo(8.029f, 2.432f)
      curveTo(7.521f, 2.505f, 7.055f, 2.927f, 6.942f, 3.418f)
      curveTo(6.672f, 4.582f, 8.079f, 5.385f, 9.056f, 4.734f)
      curveTo(10.261f, 3.933f, 9.467f, 2.226f, 8.029f, 2.432f)
      verticalLineTo(2.432f)
      close()
      moveTo(13.535f, 2.477f)
      curveTo(11.789f, 2.634f, 12.072f, 5.142f, 13.837f, 4.916f)
      curveTo(15.489f, 4.705f, 15.194f, 2.329f, 13.535f, 2.477f)
      close()
      moveTo(7.949f, 8.626f)
      horizontalLineTo(2.558f)
      lineTo(2.49f, 8.693f)
      verticalLineTo(10.366f)
      horizontalLineTo(7.949f)
      verticalLineTo(8.626f)
      close()
      moveTo(19.459f, 8.626f)
      horizontalLineTo(14.0f)
      verticalLineTo(10.366f)
      horizontalLineTo(19.459f)
      verticalLineTo(8.626f)
      close()
      moveTo(9.587f, 12.822f)
      horizontalLineTo(3.537f)
      verticalLineTo(21.571f)
      horizontalLineTo(9.587f)
      verticalLineTo(12.822f)
      close()
      moveTo(18.458f, 12.822f)
      horizontalLineTo(12.362f)
      verticalLineTo(21.571f)
      horizontalLineTo(18.458f)
      verticalLineTo(12.822f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(18.457f, 12.822f)
      horizontalLineTo(12.361f)
      verticalLineTo(21.571f)
      horizontalLineTo(18.457f)
      verticalLineTo(12.822f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(9.588f, 12.822f)
      horizontalLineTo(3.537f)
      verticalLineTo(21.571f)
      horizontalLineTo(9.588f)
      verticalLineTo(12.822f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(19.459f, 8.626f)
      horizontalLineTo(14.0f)
      verticalLineTo(10.367f)
      horizontalLineTo(19.459f)
      verticalLineTo(8.626f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(7.949f, 8.626f)
      verticalLineTo(10.367f)
      horizontalLineTo(2.49f)
      verticalLineTo(8.693f)
      lineTo(2.558f, 8.626f)
      horizontalLineTo(7.949f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(8.03f, 2.432f)
      curveTo(9.468f, 2.227f, 10.262f, 3.933f, 9.056f, 4.735f)
      curveTo(8.079f, 5.386f, 6.673f, 4.582f, 6.942f, 3.419f)
      curveTo(7.056f, 2.927f, 7.521f, 2.506f, 8.03f, 2.433f)
      verticalLineTo(2.432f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(13.536f, 2.477f)
      curveTo(15.195f, 2.329f, 15.49f, 4.706f, 13.838f, 4.916f)
      curveTo(12.073f, 5.142f, 11.79f, 2.634f, 13.536f, 2.477f)
      close()
    }
  }
}.build()
