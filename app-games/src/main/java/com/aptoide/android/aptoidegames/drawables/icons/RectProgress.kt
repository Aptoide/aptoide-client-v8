package com.aptoide.android.aptoidegames.drawables.icons

import android.graphics.PointF
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.primary
import kotlin.math.min

@Preview
@Composable
fun TestRectProgress() {
  Image(
    imageVector = getRectProgress(
      progress = 0.5f,
      progressOffset = 0.3f
    ),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

private const val sideLength = 36f
private const val totalLength = 4 * sideLength - 4f

fun getRectProgress(
  progress: Float = 1f,
  progressOffset: Float = 0f,
): ImageVector = ImageVector.Builder(
  name = "ProgressSmall",
  defaultWidth = 40.dp,
  defaultHeight = 40.dp,
  viewportWidth = 40f,
  viewportHeight = 40f,
).apply {
  var remainingProgressOffset = progressOffset * totalLength + 20f
  var remainingProgressLength = progress * totalLength

  path(
    stroke = SolidColor(primary),
    strokeLineWidth = 4f,
    strokeLineCap = StrokeCap.Square,
  ) {
    moveTo(38f, 2f)
    var index = 0
    while (remainingProgressOffset > 0) {
      val point = calculateRelativeLineEnd(
        index = index,
        length = min(sideLength, remainingProgressOffset)
      )
      moveToRelative(point.x, point.y)
      if (remainingProgressOffset < sideLength) break
      remainingProgressOffset -= sideLength
      index = (index + 1) % 4
    }
    while (remainingProgressLength > 0) {
      val length = min(sideLength - remainingProgressOffset, remainingProgressLength)
      val point = calculateRelativeLineEnd(index = index, length = length)
      lineToRelative(point.x, point.y)
      remainingProgressLength -= length
      remainingProgressOffset = 0f
      index = (index + 1) % 4
    }
  }
}.build()

private fun calculateRelativeLineEnd(
  index: Int,
  length: Float,
): PointF {
  return when (index) {
    0 -> PointF(-length, 0f)
    1 -> PointF(0f, length)
    2 -> PointF(length, 0f)
    3 -> PointF(0f, -length)
    else -> PointF(0f, 0f)
  }
}
