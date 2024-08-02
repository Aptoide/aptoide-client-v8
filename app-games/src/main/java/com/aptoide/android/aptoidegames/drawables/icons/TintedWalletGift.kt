package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestTintedWalletGift() {
  Image(
    imageVector = getTintedWalletGift(Color.Green, Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getTintedWalletGift(
  iconColor: Color,
  outlineColor: Color,
): ImageVector = ImageVector.Builder(
  name = "TintedWalletGift",
  defaultWidth = 20.dp,
  defaultHeight = 20.dp,
  viewportWidth = 20f,
  viewportHeight = 20f,
).apply {
  path(
    fill = SolidColor(iconColor),
  ) {
    moveTo(16.6668f, 9.16781f)
    verticalLineTo(18.3345f)
    horizontalLineTo(3.3335f)
    verticalLineTo(9.16781f)
    horizontalLineTo(1.66683f)
    verticalLineTo(4.16781f)
    horizontalLineTo(6.00017f)
    curveTo(5.94461f, 4.02892f, 5.90294f, 3.8935f, 5.87517f, 3.76156f)
    curveTo(5.84739f, 3.62961f, 5.8335f, 3.48725f, 5.8335f, 3.33447f)
    curveTo(5.8335f, 2.64003f, 6.07655f, 2.04975f, 6.56267f, 1.56364f)
    curveTo(7.04878f, 1.07753f, 7.63905f, 0.834473f, 8.3335f, 0.834473f)
    curveTo(8.65294f, 0.834473f, 8.95155f, 0.890028f, 9.22933f, 1.00114f)
    curveTo(9.50711f, 1.11225f, 9.76405f, 1.27892f, 10.0002f, 1.50114f)
    curveTo(10.2363f, 1.29281f, 10.4932f, 1.12961f, 10.771f, 1.01156f)
    curveTo(11.0488f, 0.8935f, 11.3474f, 0.834473f, 11.6668f, 0.834473f)
    curveTo(12.3613f, 0.834473f, 12.9516f, 1.07753f, 13.4377f, 1.56364f)
    curveTo(13.9238f, 2.04975f, 14.1668f, 2.64003f, 14.1668f, 3.33447f)
    curveTo(14.1668f, 3.48725f, 14.1564f, 3.63308f, 14.1356f, 3.77197f)
    curveTo(14.1147f, 3.91086f, 14.0696f, 4.04281f, 14.0002f, 4.16781f)
    horizontalLineTo(18.3335f)
    verticalLineTo(9.16781f)
    horizontalLineTo(16.6668f)
    close()
  }
  path(
    fill = SolidColor(outlineColor),
  ) {
    moveTo(16.6668f, 18.3345f)
    verticalLineTo(9.16781f)
    horizontalLineTo(18.3335f)
    verticalLineTo(4.16781f)
    horizontalLineTo(14.0002f)
    curveTo(14.0696f, 4.04281f, 14.1147f, 3.91086f, 14.1356f, 3.77197f)
    curveTo(14.1564f, 3.63308f, 14.1668f, 3.48725f, 14.1668f, 3.33447f)
    curveTo(14.1668f, 2.64003f, 13.9238f, 2.04975f, 13.4377f, 1.56364f)
    curveTo(12.9516f, 1.07753f, 12.3613f, 0.834473f, 11.6668f, 0.834473f)
    curveTo(11.3474f, 0.834473f, 11.0488f, 0.8935f, 10.771f, 1.01156f)
    curveTo(10.4932f, 1.12961f, 10.2363f, 1.29281f, 10.0002f, 1.50114f)
    curveTo(9.76405f, 1.27892f, 9.50711f, 1.11225f, 9.22933f, 1.00114f)
    curveTo(8.95155f, 0.890028f, 8.65294f, 0.834473f, 8.3335f, 0.834473f)
    curveTo(7.63905f, 0.834473f, 7.04878f, 1.07753f, 6.56267f, 1.56364f)
    curveTo(6.07655f, 2.04975f, 5.8335f, 2.64003f, 5.8335f, 3.33447f)
    curveTo(5.8335f, 3.48725f, 5.84739f, 3.62961f, 5.87517f, 3.76156f)
    curveTo(5.90294f, 3.8935f, 5.94461f, 4.02892f, 6.00017f, 4.16781f)
    horizontalLineTo(1.66683f)
    verticalLineTo(9.16781f)
    horizontalLineTo(3.3335f)
    verticalLineTo(18.3345f)
    horizontalLineTo(16.6668f)
    close()
    moveTo(8.3335f, 2.50114f)
    curveTo(8.56961f, 2.50114f, 8.76753f, 2.581f, 8.92725f, 2.74072f)
    curveTo(9.08697f, 2.90044f, 9.16683f, 3.09836f, 9.16683f, 3.33447f)
    curveTo(9.16683f, 3.57058f, 9.08697f, 3.7685f, 8.92725f, 3.92822f)
    curveTo(8.76753f, 4.08794f, 8.56961f, 4.16781f, 8.3335f, 4.16781f)
    curveTo(8.09739f, 4.16781f, 7.89947f, 4.08794f, 7.73975f, 3.92822f)
    curveTo(7.58003f, 3.7685f, 7.50017f, 3.57058f, 7.50017f, 3.33447f)
    curveTo(7.50017f, 3.09836f, 7.58003f, 2.90044f, 7.73975f, 2.74072f)
    curveTo(7.89947f, 2.581f, 8.09739f, 2.50114f, 8.3335f, 2.50114f)
    close()
    moveTo(12.5002f, 3.33447f)
    curveTo(12.5002f, 3.57058f, 12.4203f, 3.7685f, 12.2606f, 3.92822f)
    curveTo(12.1009f, 4.08794f, 11.9029f, 4.16781f, 11.6668f, 4.16781f)
    curveTo(11.4307f, 4.16781f, 11.2328f, 4.08794f, 11.0731f, 3.92822f)
    curveTo(10.9134f, 3.7685f, 10.8335f, 3.57058f, 10.8335f, 3.33447f)
    curveTo(10.8335f, 3.09836f, 10.9134f, 2.90044f, 11.0731f, 2.74072f)
    curveTo(11.2328f, 2.581f, 11.4307f, 2.50114f, 11.6668f, 2.50114f)
    curveTo(11.9029f, 2.50114f, 12.1009f, 2.581f, 12.2606f, 2.74072f)
    curveTo(12.4203f, 2.90044f, 12.5002f, 3.09836f, 12.5002f, 3.33447f)
    close()
    moveTo(16.6668f, 5.83447f)
    verticalLineTo(7.50114f)
    horizontalLineTo(10.8335f)
    verticalLineTo(5.83447f)
    horizontalLineTo(16.6668f)
    close()
    moveTo(10.8335f, 16.6678f)
    verticalLineTo(9.16781f)
    horizontalLineTo(15.0002f)
    verticalLineTo(16.6678f)
    horizontalLineTo(10.8335f)
    close()
    moveTo(9.16683f, 16.6678f)
    horizontalLineTo(5.00017f)
    verticalLineTo(9.16781f)
    horizontalLineTo(9.16683f)
    verticalLineTo(16.6678f)
    close()
    moveTo(3.3335f, 7.50114f)
    verticalLineTo(5.83447f)
    horizontalLineTo(9.16683f)
    verticalLineTo(7.50114f)
    horizontalLineTo(3.3335f)
    close()
  }
}.build()
