package com.aptoide.android.aptoidegames.drawables.backgrounds.myiconpack

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
fun ApkfyAppIconBackgroundPreview() {
  Image(
    imageVector = getApkfyAppIconBackground(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getApkfyAppIconBackground(
  color1: Color = Palette.White,
  color2: Color = Palette.Primary,
  color3: Color = Palette.GreyLight
): ImageVector =
  ImageVector.Builder(
    name = "ApkfyAppIconBackground",
    defaultWidth = 168.0.dp,
    defaultHeight = 96.0.dp,
    viewportWidth = 168.0f,
    viewportHeight = 96.0f
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
      moveTo(0.0f, 40.0f)
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
      moveTo(152.0f, 0.0f)
      lineToRelative(-0.0f, 48.0f)
      lineToRelative(-16.0f, 0.0f)
      lineToRelative(-0.0f, -48.0f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(120.0f, 80.0f)
      horizontalLineToRelative(23.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-23.0f)
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
      moveTo(17.0f, 8.0f)
      horizontalLineToRelative(23.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-23.0f)
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
      moveTo(16.0f, 72.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
  }.build()
