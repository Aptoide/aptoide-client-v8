package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.hexagons

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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestHexagonLevelCurrent() {
  Image(
    imageVector = getHexagonLevelCurrent(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getHexagonLevelCurrent(): ImageVector = ImageVector.Builder(
  name = "HexagonLevelCurrent",
  defaultWidth = 22.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 22.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF1E1E26)), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(20.333f, 7.22f)
    verticalLineTo(16.779f)
    lineTo(10.666f, 22.82f)
    lineTo(1.0f, 16.778f)
    verticalLineTo(7.221f)
    lineTo(10.666f, 1.179f)
    lineTo(20.333f, 7.22f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.2f,
    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(20.333f, 7.22f)
    verticalLineTo(16.779f)
    lineTo(10.666f, 22.82f)
    lineTo(1.0f, 16.778f)
    verticalLineTo(7.221f)
    lineTo(10.666f, 1.179f)
    lineTo(20.333f, 7.22f)
    close()
  }
  path(
    fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFFC93E)),
    strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(20.333f, 7.22f)
    verticalLineTo(16.779f)
    lineTo(10.666f, 22.82f)
    lineTo(1.0f, 16.778f)
    verticalLineTo(7.221f)
    lineTo(10.666f, 1.179f)
    lineTo(20.333f, 7.22f)
    close()
  }
  path(
    fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFFC93E)),
    strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(6.545f, 12.63f)
    lineTo(9.071f, 14.729f)
    lineTo(14.691f, 9.271f)
  }
}.build()
