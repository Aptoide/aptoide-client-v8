package com.aptoide.android.aptoidegames.drawables.backgrounds.myiconpack

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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestAppViewBonusGiftBackground() {
  Image(
    imageVector = getAppViewBonusGiftBackground(),
    contentDescription = null,
  )
}

fun getAppViewBonusGiftBackground(color: Color = Palette.Secondary): ImageVector =
  ImageVector.Builder(
    name = "AppViewBonusGiftBackground",
    defaultWidth = 40.0.dp,
    defaultHeight = 40.0.dp,
    viewportWidth = 40.0f,
    viewportHeight = 40.0f
  ).apply {
    path(
      fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(0.0f, 34.0f)
      horizontalLineTo(6.0f)
      verticalLineTo(40.0f)
      horizontalLineTo(27.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(40.0f)
      verticalLineTo(13.0f)
      horizontalLineTo(35.0f)
      verticalLineTo(0.0f)
      horizontalLineTo(0.0f)
      verticalLineTo(34.0f)
      close()
    }
  }.build()
