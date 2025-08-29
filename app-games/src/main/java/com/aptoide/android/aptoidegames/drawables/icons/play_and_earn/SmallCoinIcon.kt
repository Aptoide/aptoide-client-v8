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
private fun TestSmallCoinIcon() {
  Image(
    imageVector = getSmallCoinIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getSmallCoinIcon(): ImageVector = ImageVector.Builder(
  name = "SmallCoin",
  defaultWidth = 11.0.dp,
  defaultHeight = 12.0.dp,
  viewportWidth = 11.0f,
  viewportHeight = 12.0f
).apply {
  path(
    fill = SolidColor(Color(0xFFFFEA04)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 3.333f)
    lineTo(5.333f, 0.0f)
    lineTo(10.667f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.333f, 12.0f)
    lineTo(0.0f, 8.667f)
    verticalLineTo(3.333f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFC93E)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(5.333f, 2.0f)
    verticalLineTo(0.0f)
    lineTo(0.0f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.333f, 12.0f)
    verticalLineTo(10.0f)
    lineTo(2.0f, 7.667f)
    verticalLineTo(4.333f)
    lineTo(5.333f, 2.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(5.334f, 2.0f)
    verticalLineTo(0.0f)
    lineTo(10.667f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.334f, 12.0f)
    verticalLineTo(10.0f)
    lineTo(8.667f, 7.667f)
    verticalLineTo(4.333f)
    lineTo(5.334f, 2.0f)
    close()
  }
}.build()
