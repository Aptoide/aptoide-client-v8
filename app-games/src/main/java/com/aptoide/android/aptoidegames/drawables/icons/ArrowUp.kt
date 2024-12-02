package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestArrowUp() {
  Image(
    imageVector = getArrowUp(Palette.GreyLight),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .background(Palette.White)
  )
}

fun getArrowUp(color: Color): ImageVector =
  ImageVector.Builder(
    name = "ArrowUp",
    defaultWidth = 16.0.dp,
    defaultHeight = 16.0.dp,
    viewportWidth = 16.0f,
    viewportHeight = 16.0f
  ).apply {
    group {
      path(
        fill = SolidColor(color),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = EvenOdd
      ) {
        moveTo(-0.0f, 0.0f)
        lineTo(0.0f, 16.0f)
        lineTo(16.0f, 16.0f)
        lineTo(16.0f, -0.0f)
        lineTo(-0.0f, 0.0f)
        close()
        moveTo(8.3134f, 5.6468f)
        lineTo(5.0714f, 8.8889f)
        lineTo(11.5556f, 8.8889f)
        lineTo(8.3134f, 5.6468f)
        close()
      }
    }
  }
    .build()
