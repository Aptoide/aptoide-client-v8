package com.aptoide.android.aptoidegames.drawables.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestLevelUpBackground() {
  Image(
    imageVector = getExchangeSuccessBackground(Palette.Yellow50),
    contentDescription = null,
  )
}

fun getExchangeSuccessBackground(color: Color): ImageVector =
  ImageVector.Builder(
    name = "ExchangeSuccessBackground",
    defaultWidth = 878.dp,
    defaultHeight = 962.dp,
    viewportWidth = 878f,
    viewportHeight = 962f
  ).apply {
    path(
      fill = SolidColor(color),
      fillAlpha = 0.05f,
      strokeAlpha = 0.05f
    ) {
      moveTo(437.85f, 204f)
      lineTo(187f, 367.89f)
      verticalLineTo(603.34f)
      lineTo(437.85f, 758f)
      lineTo(691f, 603.34f)
      verticalLineTo(367.89f)
      lineTo(437.85f, 204f)
      close()
    }
    path(
      fill = SolidColor(color),
      fillAlpha = 0.1f,
      strokeAlpha = 0.1f
    ) {
      moveTo(438.4f, 337f)
      lineTo(307f, 423.38f)
      verticalLineTo(547.48f)
      lineTo(438.4f, 629f)
      lineTo(571f, 547.48f)
      verticalLineTo(423.38f)
      lineTo(438.4f, 337f)
      close()
    }
    path(
      fill = SolidColor(color),
      fillAlpha = 0.05f,
      strokeAlpha = 0.05f
    ) {
      moveTo(436.99f, 0f)
      lineTo(0f, 284.59f)
      verticalLineTo(693.44f)
      lineTo(436.99f, 962f)
      lineTo(878f, 693.44f)
      verticalLineTo(284.59f)
      lineTo(436.99f, 0f)
      close()
    }
  }.build()
