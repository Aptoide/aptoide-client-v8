package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestLeftArrow() {
  Image(
    imageVector = getLeftArrow(Color.LightGray, Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLeftArrow(color: Color, bgColor: Color): ImageVector = ImageVector.Builder(
  name = "LeftArrow",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f
).apply {
  group {
    path(
      fill = SolidColor(bgColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(8.61554f, 8.61537f)
      horizontalLineTo(23.38474f)
      verticalLineTo(24.61537f)
      horizontalLineTo(8.61554f)
      verticalLineTo(8.61537f)
      close()
    }
    path(
      fill = SolidColor(color),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.EvenOdd
    ) {
      moveTo(-0.615236f, 32.6154f)
      lineTo(32.6155f, 32.6154f)
      lineTo(32.6155f, -0.61537f)
      lineTo(-0.615231f, -0.615373f)
      lineTo(-0.615236f, 32.6154f)
      close()
      moveTo(11.1127f, 15.349f)
      lineTo(17.8463f, 22.0826f)
      lineTo(17.8463f, 8.6154f)
      lineTo(11.1127f, 15.349f)
      close()
    }
  }
}.build()
