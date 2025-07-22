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

@Preview
@Composable
private fun TestLeaderboardIcon() {
  Image(
    imageVector = getLeaderboardIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLeaderboardIcon(): ImageVector = ImageVector.Builder(
  name = "Leaderboard",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
).apply {
  group {
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(2.0f, 21.0f)
      verticalLineTo(12.5f)
      horizontalLineTo(7.5f)
      verticalLineTo(21.0f)
      horizontalLineTo(2.0f)
      close()
      moveTo(9.25f, 21.0f)
      verticalLineTo(8.0f)
      horizontalLineTo(14.75f)
      verticalLineTo(21.0f)
      horizontalLineTo(9.25f)
      close()
      moveTo(16.5f, 21.0f)
      verticalLineTo(3.0f)
      horizontalLineTo(22.0f)
      verticalLineTo(21.0f)
      horizontalLineTo(16.5f)
      close()
    }
  }
}.build()
