package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

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
fun TestRightArrow() {
  Image(
    imageVector = getRightArrow(Color.White, Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getRightArrow(
  color: Color,
  bgColor: Color,
): ImageVector = ImageVector.Builder(
  name = "arrow_right",
  defaultWidth = 16.dp,
  defaultHeight = 16.dp,
  viewportWidth = 16f,
  viewportHeight = 16f
).apply {
  group {
    // Background rectangle
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
      moveTo(0f, 0f)
      horizontalLineTo(16f)
      verticalLineTo(16f)
      horizontalLineTo(0f)
      verticalLineTo(0f)
      close()
    }
    // Arrow shape
    path(
      fill = SolidColor(color),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(5.33342f, 3.33342f)
      lineTo(12.6667f, 8.00008f)
      lineTo(5.33341f, 12.6667f)
      lineTo(5.33342f, 3.33342f)
      close()
    }
  }
}.build()
