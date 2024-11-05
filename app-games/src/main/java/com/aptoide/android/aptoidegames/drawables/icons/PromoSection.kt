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
fun TestPromoSection() {
  Image(
    imageVector = getPromoSection(Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPromoSection(
  backgroundColor: Color,
  themeColor: Color,
): ImageVector = ImageVector.Builder(
  name = "Promosection",
  defaultWidth = 288.0.dp,
  defaultHeight = 40.0.dp,
  viewportWidth = 288.0f,
  viewportHeight = 40.0f
).apply {
  path(
    fill = SolidColor(backgroundColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 0.0f)
    horizontalLineToRelative(288.0f)
    verticalLineToRelative(40.0f)
    horizontalLineToRelative(-288.0f)
    close()
  }
  path(
    fill = SolidColor(themeColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(240.0f, 0.0f)
    horizontalLineToRelative(48.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-48.0f)
    close()
  }
}.build()
