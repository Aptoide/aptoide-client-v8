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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestThumbUpIcon() {
  Image(
    imageVector = getThumbUpIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getThumbUpIcon(color: Color = Palette.White): ImageVector = ImageVector.Builder(
  name = "ThumbUp",
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
      pathFillType = NonZero
    ) {
      moveTo(7.429f, 21.0f)
      verticalLineTo(9.3f)
      lineTo(13.762f, 3.0f)
      lineTo(15.436f, 4.665f)
      lineTo(14.259f, 9.3f)
      horizontalLineTo(21.0f)
      verticalLineTo(13.26f)
      lineTo(17.698f, 21.0f)
      horizontalLineTo(7.429f)
      close()
      moveTo(2.0f, 21.0f)
      verticalLineTo(9.3f)
      horizontalLineTo(5.619f)
      verticalLineTo(21.0f)
      horizontalLineTo(2.0f)
      close()
    }
  }
}.build()
