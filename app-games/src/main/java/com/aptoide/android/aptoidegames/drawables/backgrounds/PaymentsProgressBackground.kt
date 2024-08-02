package com.aptoide.android.aptoidegames.drawables.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
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
fun TestPaymentsProgressBackground() {
  Image(
    imageVector = getPaymentsProgressBackground(Color.Magenta, Color.Cyan),
    contentDescription = null,
  )
}

fun getPaymentsProgressBackground(
  color1: Color,
  color2: Color,
): ImageVector = ImageVector.Builder(
  name = "PaymentsProgressBackground",
  defaultWidth = 328.0.dp,
  defaultHeight = 264.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 264.0f
).apply {
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(48.0f, 16.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(248.0f, 56.0f)
    horizontalLineToRelative(56.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-56.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(80.0f, 0.0f)
    horizontalLineToRelative(24.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-24.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(96.0f, 248.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
}.build()
