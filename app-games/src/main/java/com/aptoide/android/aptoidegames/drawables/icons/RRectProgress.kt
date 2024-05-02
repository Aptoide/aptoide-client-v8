package com.aptoide.android.aptoidegames.drawables.icons

import android.graphics.PointF
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.richOrange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Preview
@Composable
fun TestRRectProgress() {
  Image(
    imageVector = getRRectProgress(
      progress = 0.5f,
      progressOffset = 0.3f,
      cornerRadius = 8f / 40
    ),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getRRectProgress(
  progress: Float = 1f,
  progressOffset: Float = 0f,
  cornerRadius: Float = 8f / 40,
): ImageVector = ImageVector.Builder(
  name = "ProgressSmall",
  defaultWidth = 40.dp,
  defaultHeight = 40.dp,
  viewportWidth = 40f,
  viewportHeight = 40f,
).apply {
  val radius = cornerRadius * 40 - 2
  val halfSideLength = 18 - radius
  val cornerLength = (PI * radius / 2).toFloat()
  val totalLength = halfSideLength * 8 + cornerLength * 4
  var remainingProgressOffset = (progressOffset * totalLength)
  var remainingProgressLength = (progress * totalLength)

  path(
    stroke = SolidColor(richOrange),
    strokeLineWidth = 4f,
    strokeLineCap = StrokeCap.Butt,
  ) {
    moveTo(20f, 2f)
    var index = 0
    while (remainingProgressOffset > 0) {
      when (index) {
        0, 2, 3, 5, 6, 8, 9, 11 -> {
          val point = calculateLineEnd(index, 0f, min(halfSideLength, remainingProgressOffset))
          moveToRelative(point.x, point.y)
          if (remainingProgressOffset < halfSideLength) break
          remainingProgressOffset -= halfSideLength
        }

        1, 4, 7, 10 -> {
          val point = calculateArcEnd(index, radius, 0f, min(cornerLength, remainingProgressOffset))
          moveToRelative(point.x, point.y)
          if (remainingProgressOffset < cornerLength) break
          remainingProgressOffset -= cornerLength
        }
      }
      index++
    }
    while (remainingProgressLength > 0) {
      when (index) {
        0, 2, 3, 5, 6, 8, 9, 11 -> {
          val point = calculateLineEnd(
            index,
            remainingProgressOffset,
            min(halfSideLength, remainingProgressLength)
          )
          lineToRelative(point.x, point.y)
          remainingProgressLength -= halfSideLength - remainingProgressOffset
        }

        1, 4, 7, 10 -> {
          val point = calculateArcEnd(
            index,
            radius,
            remainingProgressOffset,
            min(cornerLength, remainingProgressLength)
          )
          myArcToRelative(radius, point.x, point.y)
          remainingProgressLength -= cornerLength - remainingProgressOffset
        }
      }
      remainingProgressOffset = 0f
      index = (index + 1) % 12
    }
  }
}.build()

private fun PathBuilder.myArcToRelative(radius: Float, dx1: Float, dy1: Float): PathBuilder =
  arcToRelative(
    radius,
    radius,
    theta = 0f,
    isMoreThanHalf = false,
    isPositiveArc = false,
    dx1 = dx1,
    dy1 = dy1
  )

private fun calculateLineEnd(
  index: Int,
  offset: Float,
  length: Float,
): PointF = when (index) {
  0 -> PointF(offset - length, 0f)
  2 -> PointF(0f, length - offset)
  3 -> PointF(0f, length - offset)
  5 -> PointF(length - offset, 0f)
  6 -> PointF(length - offset, 0f)
  8 -> PointF(0f, offset - length)
  9 -> PointF(0f, offset - length)
  11 -> PointF(offset - length, 0f)
  else -> PointF(0f, 0f)
}

private fun calculateArcEnd(
  index: Int,
  radius: Float,
  offset: Float,
  length: Float,
): PointF = ((offset / radius) to (length / radius))
  .let {
    PointF(sin(it.first), cos(it.first)) to
      PointF(sin(it.second), cos(it.second))
  }
  .run {
    when (index) {
      1 -> PointF((first.x - second.x) * radius, (first.y - second.y) * radius)
      4 -> PointF((first.y - second.y) * radius, (second.x - first.x) * radius)
      7 -> PointF((second.x - first.x) * radius, (second.y - first.y) * radius)
      10 -> PointF((second.y - first.y) * radius, (first.x - second.x) * radius)
      else -> PointF((second.x - first.x) * radius, (second.y - first.y) * radius)
    }
  }
