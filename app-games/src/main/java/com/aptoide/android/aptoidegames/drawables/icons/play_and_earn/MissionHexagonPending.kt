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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestMissionHexagonPendingIcon() {
  Image(
    imageVector = getMissionHexagonPendingIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getMissionHexagonPendingIcon(): ImageVector = ImageVector.Builder(
  name = "MissionHexagonPending",
  defaultWidth = 21.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 21.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF312D35)), stroke = SolidColor(Color(0xFF913DD8)),
    fillAlpha = 0.6f, strokeAlpha = 0.6f, strokeLineWidth = 2.0f, strokeLineCap =
      Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(20.0f, 7.216f)
    verticalLineTo(16.783f)
    lineTo(10.5f, 22.815f)
    lineTo(1.0f, 16.783f)
    verticalLineTo(7.216f)
    lineTo(10.5f, 1.184f)
    lineTo(20.0f, 7.216f)
    close()
  }
}.build()
