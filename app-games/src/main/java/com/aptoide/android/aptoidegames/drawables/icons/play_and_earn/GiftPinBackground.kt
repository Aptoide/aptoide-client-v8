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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestGiftPinBackground() {
  Image(
    imageVector = getGiftPinBackground(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGiftPinBackground(color: Color = Palette.Secondary): ImageVector = ImageVector.Builder(
  name = "GiftPinBackground",
  defaultWidth = 32.0.dp,
  defaultHeight = 41.0.dp,
  viewportWidth = 32.0f,
  viewportHeight = 41.0f
).apply {
  path(
    fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.0f, 40.5f)
    lineTo(3.0f, 25.0f)
    lineTo(29.0f, 25.0f)
    lineTo(16.0f, 40.5f)
    close()
  }
  path(
    fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.0f, 16.0f)
    moveToRelative(-16.0f, 0.0f)
    arcToRelative(16.0f, 16.0f, 0.0f, true, true, 32.0f, 0.0f)
    arcToRelative(16.0f, 16.0f, 0.0f, true, true, -32.0f, 0.0f)
  }
}.build()
