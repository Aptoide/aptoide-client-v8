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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestDiamondShine() {
  Image(
    imageVector = getDiamondShine(Palette.White),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getDiamondShine(color: Color = Palette.White): ImageVector = ImageVector.Builder(
  name = "DiamondShine",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
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
      moveTo(4.574f, 7.0f)
      lineTo(2.449f, 4.875f)
      lineTo(3.874f, 3.475f)
      lineTo(5.999f, 5.6f)
      lineTo(4.574f, 7.0f)
      close()
      moveTo(10.999f, 5.0f)
      verticalLineTo(2.0f)
      horizontalLineTo(12.999f)
      verticalLineTo(5.0f)
      horizontalLineTo(10.999f)
      close()
      moveTo(19.374f, 7.0f)
      lineTo(17.949f, 5.575f)
      lineTo(20.074f, 3.45f)
      lineTo(21.499f, 4.875f)
      lineTo(19.374f, 7.0f)
      close()
      moveTo(11.999f, 22.0f)
      lineTo(3.924f, 14.0f)
      horizontalLineTo(20.074f)
      lineTo(11.999f, 22.0f)
      close()
      moveTo(7.999f, 7.0f)
      horizontalLineTo(15.999f)
      lineTo(20.099f, 12.0f)
      horizontalLineTo(3.899f)
      lineTo(7.999f, 7.0f)
      close()
    }
  }
}.build()
