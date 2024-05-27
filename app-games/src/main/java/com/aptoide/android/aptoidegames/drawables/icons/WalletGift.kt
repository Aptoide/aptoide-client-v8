package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestWalletGift() {
  Image(
    imageVector = getWalletGift(Palette.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp).background(Palette.White)
  )
}

fun getWalletGift(color: Color): ImageVector = Builder(
  name = "FeaturedSeasonalAndGifts",
  defaultWidth = 17.0.dp,
  defaultHeight = 16.0.dp,
  viewportWidth = 17.0f,
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
      pathFillType = NonZero
    ) {
      moveTo(3.1667f, 14.6666f)
      verticalLineTo(7.3333f)
      horizontalLineTo(1.8334f)
      verticalLineTo(3.3333f)
      horizontalLineTo(5.3f)
      curveTo(5.2445f, 3.2333f, 5.2084f, 3.1277f, 5.1917f, 3.0166f)
      curveTo(5.175f, 2.9055f, 5.1667f, 2.7889f, 5.1667f, 2.6666f)
      curveTo(5.1667f, 2.1111f, 5.3611f, 1.6388f, 5.75f, 1.25f)
      curveTo(6.1389f, 0.8611f, 6.6111f, 0.6666f, 7.1667f, 0.6666f)
      curveTo(7.4223f, 0.6666f, 7.6611f, 0.7138f, 7.8834f, 0.8083f)
      curveTo(8.1056f, 0.9027f, 8.3111f, 1.0333f, 8.5f, 1.2f)
      curveTo(8.6889f, 1.0222f, 8.8945f, 0.8888f, 9.1167f, 0.8f)
      curveTo(9.3389f, 0.7111f, 9.5778f, 0.6666f, 9.8334f, 0.6666f)
      curveTo(10.3889f, 0.6666f, 10.8612f, 0.8611f, 11.25f, 1.25f)
      curveTo(11.6389f, 1.6388f, 11.8334f, 2.1111f, 11.8334f, 2.6666f)
      curveTo(11.8334f, 2.7889f, 11.8223f, 2.9027f, 11.8f, 3.0083f)
      curveTo(11.7778f, 3.1139f, 11.7445f, 3.2222f, 11.7f, 3.3333f)
      horizontalLineTo(15.1667f)
      verticalLineTo(7.3333f)
      horizontalLineTo(13.8334f)
      verticalLineTo(14.6666f)
      horizontalLineTo(3.1667f)
      close()
      moveTo(9.8334f, 2.0f)
      curveTo(9.6445f, 2.0f, 9.4861f, 2.0638f, 9.3584f, 2.1916f)
      curveTo(9.2306f, 2.3194f, 9.1667f, 2.4777f, 9.1667f, 2.6666f)
      curveTo(9.1667f, 2.8555f, 9.2306f, 3.0138f, 9.3584f, 3.1416f)
      curveTo(9.4861f, 3.2694f, 9.6445f, 3.3333f, 9.8334f, 3.3333f)
      curveTo(10.0223f, 3.3333f, 10.1806f, 3.2694f, 10.3084f, 3.1416f)
      curveTo(10.4362f, 3.0138f, 10.5f, 2.8555f, 10.5f, 2.6666f)
      curveTo(10.5f, 2.4777f, 10.4362f, 2.3194f, 10.3084f, 2.1916f)
      curveTo(10.1806f, 2.0638f, 10.0223f, 2.0f, 9.8334f, 2.0f)
      close()
      moveTo(6.5f, 2.6666f)
      curveTo(6.5f, 2.8555f, 6.5639f, 3.0138f, 6.6917f, 3.1416f)
      curveTo(6.8195f, 3.2694f, 6.9778f, 3.3333f, 7.1667f, 3.3333f)
      curveTo(7.3556f, 3.3333f, 7.5139f, 3.2694f, 7.6417f, 3.1416f)
      curveTo(7.7695f, 3.0138f, 7.8334f, 2.8555f, 7.8334f, 2.6666f)
      curveTo(7.8334f, 2.4777f, 7.7695f, 2.3194f, 7.6417f, 2.1916f)
      curveTo(7.5139f, 2.0638f, 7.3556f, 2.0f, 7.1667f, 2.0f)
      curveTo(6.9778f, 2.0f, 6.8195f, 2.0638f, 6.6917f, 2.1916f)
      curveTo(6.5639f, 2.3194f, 6.5f, 2.4777f, 6.5f, 2.6666f)
      close()
      moveTo(3.1667f, 4.6666f)
      verticalLineTo(6.0f)
      horizontalLineTo(7.8334f)
      verticalLineTo(4.6666f)
      horizontalLineTo(3.1667f)
      close()
      moveTo(7.8334f, 13.3333f)
      verticalLineTo(7.3333f)
      horizontalLineTo(4.5f)
      verticalLineTo(13.3333f)
      horizontalLineTo(7.8334f)
      close()
      moveTo(9.1667f, 13.3333f)
      horizontalLineTo(12.5f)
      verticalLineTo(7.3333f)
      horizontalLineTo(9.1667f)
      verticalLineTo(13.3333f)
      close()
      moveTo(13.8334f, 6.0f)
      verticalLineTo(4.6666f)
      horizontalLineTo(9.1667f)
      verticalLineTo(6.0f)
      horizontalLineTo(13.8334f)
      close()
    }
  }
}
  .build()
