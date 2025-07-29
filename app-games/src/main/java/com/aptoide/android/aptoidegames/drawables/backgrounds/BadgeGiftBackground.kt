package com.aptoide.android.aptoidegames.drawables.backgrounds

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
private fun TestBadgeGiftBackground() {
  Image(
    imageVector = getBadgeGiftBackground(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBadgeGiftBackground(): ImageVector = ImageVector.Builder(
  name = "BadgeGiftBackground",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF913DD8)), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 12.0f)
    curveTo(0.0f, 5.373f, 5.373f, 0.0f, 12.0f, 0.0f)
    curveTo(18.627f, 0.0f, 24.0f, 5.373f, 24.0f, 12.0f)
    curveTo(24.0f, 18.627f, 18.627f, 24.0f, 12.0f, 24.0f)
    horizontalLineTo(0.0f)
    verticalLineTo(12.0f)
    close()
  }
}.build()
