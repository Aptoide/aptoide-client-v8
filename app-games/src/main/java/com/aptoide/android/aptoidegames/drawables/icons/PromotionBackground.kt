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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestPromotionBackground() {
  Image(
    imageVector = getPromotionBackground(Color.Green),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPromotionBackground(color: Color): ImageVector = ImageVector.Builder(
  name = "Promotionbackground",
  defaultWidth = 88.0.dp,
  defaultHeight = 168.0.dp,
  viewportWidth = 88.0f,
  viewportHeight = 168.0f
).apply {
  path(
    fill = SolidColor(color),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(8.0f, 0.0f)
    horizontalLineToRelative(80.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-80.0f)
    close()
  }
  path(
    fill = SolidColor(color),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 48.0f)
    horizontalLineToRelative(88.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-88.0f)
    close()
  }
  path(
    fill = SolidColor(color),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(48.0f, 146.0f)
    horizontalLineToRelative(32.0f)
    verticalLineToRelative(22.0f)
    horizontalLineToRelative(-32.0f)
    close()
  }
}
  .build()

