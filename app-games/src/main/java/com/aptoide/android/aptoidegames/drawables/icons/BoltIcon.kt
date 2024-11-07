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
fun TestBolt() {
  Image(
    imageVector = getBolt(Color.Cyan),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBolt(color: Color): ImageVector = ImageVector.Builder(
  name = "Bolt", defaultWidth = 32.0.dp,
  defaultHeight = 32.0.dp,
  viewportWidth = 32.0f,
  viewportHeight = 32.0f
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
      moveTo(11.6666f, 28.3333f)
      lineTo(12.9999f, 19.0f)
      horizontalLineTo(6.3333f)
      lineTo(18.3333f, 1.6667f)
      horizontalLineTo(20.9999f)
      lineTo(19.6666f, 12.3333f)
      horizontalLineTo(27.6666f)
      lineTo(14.3333f, 28.3333f)
      horizontalLineTo(11.6666f)
      close()
    }
    path(
      fill = SolidColor(color), stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(0.0f, 29.0f)
      horizontalLineToRelative(3.0f)
      verticalLineToRelative(3.0f)
      horizontalLineToRelative(-3.0f)
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
      moveTo(6.0f, 5.0f)
      horizontalLineToRelative(3.0f)
      verticalLineToRelative(3.0f)
      horizontalLineToRelative(-3.0f)
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
      moveTo(24.0f, 21.0f)
      horizontalLineToRelative(3.0f)
      verticalLineToRelative(3.0f)
      horizontalLineToRelative(-3.0f)
      close()
    }
  }
}
  .build()
