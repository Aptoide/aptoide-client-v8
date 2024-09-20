package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
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
fun TestForward() {
  Image(
    imageVector = getForward(Palette.Grey, Palette.AppCoinsPink),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getForward(
  color1: Color = Palette.Primary,
  color2: Color = Palette.Black
): ImageVector = ImageVector.Builder(
  name = "Arrowforward",
  defaultWidth = 32.0.dp,
  defaultHeight = 32.0.dp,
  viewportWidth = 32.0f,
  viewportHeight = 32.0f
).apply {
  group {
    path(
      fill = SolidColor(color2),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(23.3843f, 8.6155f)
      lineToRelative(-14.7692f, -0.0f)
      lineToRelative(-0.0f, 16.0f)
      lineToRelative(14.7692f, 0.0f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(32.615f, 32.6153f)
      lineTo(-0.6157f, 32.6153f)
      lineTo(-0.6157f, -0.6155f)
      lineTo(32.615f, -0.6155f)
      lineTo(32.615f, 32.6153f)
      close()
      moveTo(20.8871f, 15.3489f)
      lineTo(14.1535f, 22.0825f)
      lineTo(14.1535f, 8.6153f)
      lineTo(20.8871f, 15.3489f)
      close()
    }
  }
}.build()
