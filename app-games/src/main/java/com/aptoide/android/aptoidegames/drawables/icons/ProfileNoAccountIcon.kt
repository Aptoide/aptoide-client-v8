package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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
private fun TestProfileNoAccountIcon() {
  Image(
    imageVector = getProfileNoAccountIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getProfileNoAccountIcon(color: Color = Palette.White): ImageVector = ImageVector.Builder(
  name = "ProfileNoAccountIcon",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
).apply {
  group {
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(15.315f, 13.895f)
      horizontalLineTo(8.684f)
      verticalLineTo(7.263f)
      horizontalLineTo(15.315f)
      verticalLineTo(13.895f)
      close()
      moveTo(10.294f, 12.284f)
      horizontalLineTo(13.705f)
      verticalLineTo(8.874f)
      horizontalLineTo(10.294f)
      verticalLineTo(12.284f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(12.0f, 0.0f)
      curveTo(13.667f, 0.0f, 15.229f, 0.314f, 16.689f, 0.941f)
      curveTo(18.149f, 1.568f, 19.42f, 2.421f, 20.5f, 3.501f)
      curveTo(21.58f, 4.581f, 22.433f, 5.851f, 23.06f, 7.31f)
      curveTo(23.687f, 8.77f, 24.0f, 10.334f, 24.0f, 12.0f)
      curveTo(24.0f, 13.666f, 23.687f, 15.23f, 23.06f, 16.69f)
      curveTo(22.433f, 18.149f, 21.58f, 19.419f, 20.5f, 20.499f)
      curveTo(19.42f, 21.58f, 18.149f, 22.433f, 16.689f, 23.06f)
      curveTo(15.229f, 23.687f, 13.667f, 24.0f, 12.0f, 24.0f)
      curveTo(10.334f, 24.0f, 8.771f, 23.687f, 7.311f, 23.06f)
      curveTo(5.851f, 22.433f, 4.581f, 21.579f, 3.501f, 20.499f)
      curveTo(2.421f, 19.419f, 1.567f, 18.149f, 0.94f, 16.69f)
      curveTo(0.313f, 15.23f, 0.0f, 13.666f, 0.0f, 12.0f)
      curveTo(0.0f, 10.334f, 0.313f, 8.77f, 0.94f, 7.31f)
      curveTo(1.567f, 5.851f, 2.421f, 4.581f, 3.501f, 3.501f)
      curveTo(4.581f, 2.421f, 5.851f, 1.568f, 7.311f, 0.941f)
      curveTo(8.771f, 0.314f, 10.334f, 0.0f, 12.0f, 0.0f)
      close()
      moveTo(12.0f, 18.0f)
      curveTo(10.812f, 18.0f, 9.698f, 18.175f, 8.66f, 18.526f)
      curveTo(7.622f, 18.877f, 6.696f, 19.374f, 5.883f, 20.016f)
      curveTo(6.704f, 20.675f, 7.644f, 21.188f, 8.702f, 21.555f)
      curveTo(9.761f, 21.922f, 10.86f, 22.105f, 12.0f, 22.105f)
      curveTo(13.14f, 22.105f, 14.24f, 21.921f, 15.298f, 21.555f)
      curveTo(16.356f, 21.188f, 17.296f, 20.675f, 18.117f, 20.016f)
      curveTo(17.296f, 19.381f, 16.368f, 18.887f, 15.334f, 18.532f)
      curveTo(14.3f, 18.178f, 13.189f, 18.0f, 12.0f, 18.0f)
      close()
      moveTo(12.0f, 1.896f)
      curveTo(9.2f, 1.896f, 6.815f, 2.879f, 4.847f, 4.848f)
      curveTo(2.879f, 6.816f, 1.895f, 9.2f, 1.895f, 12.0f)
      curveTo(1.895f, 13.323f, 2.124f, 14.552f, 2.583f, 15.688f)
      curveTo(3.042f, 16.823f, 3.665f, 17.822f, 4.45f, 18.685f)
      curveTo(5.524f, 17.889f, 6.694f, 17.259f, 7.959f, 16.798f)
      curveTo(9.225f, 16.336f, 10.572f, 16.106f, 12.0f, 16.106f)
      curveTo(13.429f, 16.106f, 14.775f, 16.336f, 16.041f, 16.798f)
      curveTo(17.306f, 17.259f, 18.476f, 17.889f, 19.55f, 18.685f)
      curveTo(20.335f, 17.822f, 20.958f, 16.823f, 21.417f, 15.688f)
      curveTo(21.876f, 14.552f, 22.105f, 13.323f, 22.105f, 12.0f)
      curveTo(22.105f, 9.2f, 21.121f, 6.816f, 19.153f, 4.848f)
      curveTo(17.185f, 2.879f, 14.8f, 1.896f, 12.0f, 1.896f)
      close()
    }
  }
}.build()
