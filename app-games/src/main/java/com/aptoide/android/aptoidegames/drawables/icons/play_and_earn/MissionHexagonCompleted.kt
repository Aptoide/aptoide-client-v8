package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
private fun TestMissionHexagonCompletedIcon() {
  Image(
    imageVector = getMissionHexagonCompletedIcon(),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .shadow(
        elevation = 6.dp,
        shape = CircleShape,
        spotColor = Color(0x80C279FF),
        clip = false
      )
  )
}

fun getMissionHexagonCompletedIcon(): ImageVector = ImageVector.Builder(
  name = "MissionHexagonCompleted", defaultWidth = 21.0.dp,
  defaultHeight = 24.0.dp, viewportWidth = 21.0f, viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF913DD8)), stroke = SolidColor(Color(0xFF913DD8)),
    strokeLineWidth = 1.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(20.5f, 6.94f)
    verticalLineTo(17.059f)
    lineTo(10.5f, 23.408f)
    lineTo(0.5f, 17.059f)
    verticalLineTo(6.94f)
    lineTo(10.5f, 0.591f)
    lineTo(20.5f, 6.94f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.129f, 8.684f)
    lineTo(8.972f, 15.841f)
    lineTo(5.344f, 12.738f)
    lineTo(6.645f, 11.219f)
    lineTo(8.864f, 13.118f)
    lineTo(14.715f, 7.27f)
    lineTo(16.129f, 8.684f)
    close()
  }
}.build()
