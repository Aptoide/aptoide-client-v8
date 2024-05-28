package com.aptoide.android.aptoidegames.design_system

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay

@Preview
@Composable
fun IndeterminateCircularLoadingPreview() {
  IndeterminateCircularLoading()
}

@Composable
fun IndeterminateCircularLoading() {
  var startAngle by remember { mutableFloatStateOf(0f) }
  var sweepAngle by remember { mutableFloatStateOf(0f) }
  var progress by remember { mutableFloatStateOf(45f) }

  var fillCircle by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(180L)
      if (progress + 45f > 360f) {
        progress = 45f
        fillCircle = !fillCircle
      } else {
        progress += 45f
      }
    }
  }

  LaunchedEffect(progress) {
    if (fillCircle) {
      startAngle = -90f + (progress)
      sweepAngle = 360f - (progress)
    } else {
      startAngle = -90f
      sweepAngle = progress
    }
  }

  CircularLoading(
    size = 74.dp,
    startAngle = startAngle,
    sweepAngle = sweepAngle
  )
}

@Composable
private fun CircularLoading(
  size: Dp,
  startAngle: Float,
  sweepAngle: Float,
) {
  Canvas(modifier = Modifier.size(size)) {
    val strokeWidth = (size / 9).toPx()
    val arcSize = size.toPx() - strokeWidth
    drawArc(
      color = Palette.Primary,
      startAngle = 0f,
      sweepAngle = 360f,
      useCenter = false,
      topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
      size = Size(arcSize, arcSize),
      style = Stroke(strokeWidth)
    )
    drawArc(
      color = Palette.Primary,
      startAngle = startAngle,
      sweepAngle = sweepAngle,
      useCenter = true,
    )
  }
}
