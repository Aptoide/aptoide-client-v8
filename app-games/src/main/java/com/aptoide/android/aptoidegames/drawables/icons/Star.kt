package com.aptoide.android.aptoidegames.drawables.icons

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
fun TestRatingStar() {
  Image(
    imageVector = getRatingStar(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getRatingStar(color: Color): ImageVector = ImageVector.Builder(
  name = "Star",
  defaultWidth = 14.0.dp,
  defaultHeight = 14.0.dp,
  viewportWidth = 14.0f,
  viewportHeight = 14.0f
).apply {
  group {
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(5.1628f, 9.8146f)
      lineTo(7.0003f, 8.7063f)
      lineTo(8.8378f, 9.8292f)
      lineTo(8.3566f, 7.7292f)
      lineTo(9.9753f, 6.3292f)
      lineTo(7.8462f, 6.1396f)
      lineTo(7.0003f, 4.1563f)
      lineTo(6.1545f, 6.125f)
      lineTo(4.0253f, 6.3146f)
      lineTo(5.6441f, 7.7292f)
      lineTo(5.1628f, 9.8146f)
      close()
      moveTo(3.3982f, 12.25f)
      lineTo(4.3462f, 8.1521f)
      lineTo(1.167f, 5.3958f)
      lineTo(5.367f, 5.0313f)
      lineTo(7.0003f, 1.1667f)
      lineTo(8.6337f, 5.0313f)
      lineTo(12.8337f, 5.3958f)
      lineTo(9.6545f, 8.1521f)
      lineTo(10.6024f, 12.25f)
      lineTo(7.0003f, 10.0771f)
      lineTo(3.3982f, 12.25f)
      close()
    }
  }
}
  .build()
