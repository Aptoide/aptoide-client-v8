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
private fun TestHexagonLevelUnlocked() {
  Image(
    imageVector = getHexagonLevelUnlocked(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getHexagonLevelUnlocked(): ImageVector = ImageVector.Builder(
  name = "HexagonLevelUnlocked",
  defaultWidth = 22.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 22.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 6.667f)
    lineTo(10.667f, 0.0f)
    lineTo(21.333f, 6.667f)
    verticalLineTo(17.333f)
    lineTo(10.667f, 24.0f)
    lineTo(0.0f, 17.333f)
    verticalLineTo(6.667f)
    close()
  }
  path(
    fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF1E1E26)),
    strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(6.545f, 12.63f)
    lineTo(9.071f, 14.729f)
    lineTo(14.691f, 9.271f)
  }
  path(
    fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
    strokeAlpha = 0.2f, strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin
    = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(6.545f, 12.63f)
    lineTo(9.071f, 14.729f)
    lineTo(14.691f, 9.271f)
  }
}.build()
