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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestAGAppIcon() {
  Image(
    imageVector = getAGAppIcon(),
    contentDescription = null,
    modifier = Modifier.size(64.dp)
  )
}

fun getAGAppIcon(color: Color = Palette.Primary): ImageVector =
  ImageVector.Builder(
    name = "AGAppIcon",
    defaultWidth = 120.0.dp,
    defaultHeight = 120.0.dp,
    viewportWidth = 120.0f,
    viewportHeight = 120.0f
  ).apply {
    path(
      fill = SolidColor(Color(0xFF1E1E26)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(17.0f, 17.0f)
      horizontalLineToRelative(86.0f)
      verticalLineToRelative(86.0f)
      horizontalLineToRelative(-86.0f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF000000)),
      stroke = null,
      fillAlpha = 0.2f,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(17.0f, 17.0f)
      horizontalLineToRelative(86.0f)
      verticalLineToRelative(86.0f)
      horizontalLineToRelative(-86.0f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(17.14f, 0.0f)
      horizontalLineTo(102.86f)
      verticalLineTo(17.14f)
      horizontalLineTo(120.0f)
      verticalLineTo(111.43f)
      horizontalLineTo(102.86f)
      verticalLineTo(120.0f)
      horizontalLineTo(17.14f)
      verticalLineTo(102.86f)
      horizontalLineTo(0.0f)
      verticalLineTo(12.0f)
      horizontalLineTo(17.14f)
      verticalLineTo(0.0f)
      close()
      moveTo(85.71f, 25.71f)
      horizontalLineTo(77.14f)
      verticalLineTo(34.29f)
      horizontalLineTo(68.57f)
      verticalLineTo(44.57f)
      horizontalLineTo(60.0f)
      verticalLineTo(51.43f)
      horizontalLineTo(51.43f)
      verticalLineTo(42.86f)
      horizontalLineTo(42.86f)
      verticalLineTo(34.29f)
      horizontalLineTo(34.29f)
      verticalLineTo(25.71f)
      horizontalLineTo(25.71f)
      verticalLineTo(68.57f)
      horizontalLineTo(34.29f)
      verticalLineTo(77.14f)
      horizontalLineTo(42.86f)
      verticalLineTo(85.71f)
      horizontalLineTo(51.43f)
      verticalLineTo(94.29f)
      horizontalLineTo(68.57f)
      verticalLineTo(85.71f)
      horizontalLineTo(77.14f)
      verticalLineTo(77.14f)
      horizontalLineTo(85.71f)
      verticalLineTo(68.57f)
      horizontalLineTo(94.29f)
      verticalLineTo(34.29f)
      horizontalLineTo(85.71f)
      verticalLineTo(25.71f)
      close()
      moveTo(42.86f, 60.0f)
      verticalLineTo(68.57f)
      horizontalLineTo(34.29f)
      verticalLineTo(60.0f)
      horizontalLineTo(42.86f)
      close()
      moveTo(42.86f, 60.0f)
      horizontalLineTo(51.43f)
      verticalLineTo(51.43f)
      horizontalLineTo(42.86f)
      verticalLineTo(60.0f)
      close()
    }
  }.build()
