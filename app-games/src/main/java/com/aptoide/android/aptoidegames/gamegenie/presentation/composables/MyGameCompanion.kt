package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private fun Drawable.toBitmap(
  width: Int = intrinsicWidth,
  height: Int = intrinsicHeight,
): Bitmap {
  if (this is BitmapDrawable && bitmap != null) {
    return bitmap
  }

  val bmp = if (width > 0 && height > 0) {
    createBitmap(width, height)
  } else {
    createBitmap(1, 1) // fallback
  }
  val canvas = Canvas(bmp)

  if (this is AdaptiveIconDrawable) {
    this.background?.setBounds(0, 0, canvas.width, canvas.height)
    this.background?.draw(canvas)
    this.foreground?.setBounds(0, 0, canvas.width, canvas.height)
    this.foreground?.draw(canvas)
  } else {
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
  }

  return bmp
}

@Composable
fun GameCompanionIcon(
  game: GameCompanion,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = AGTypography.DescriptionGames,
  textSize: TextUnit = AGTypography.DescriptionGames.fontSize,
  imageSize: Int = 64,
  clickableEnabled: Boolean = true,
  onClick: (GameCompanion) -> Unit = {},
) {
  Column(
    modifier = modifier
      .then(
        if (clickableEnabled) Modifier.clickable { onClick(game) }
        else Modifier
      ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (game.image != null) {
      val painter =
        remember { BitmapPainter(game.image.toBitmap(imageSize, imageSize).asImageBitmap()) }
      Image(
        painter = painter,
        contentDescription = game.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(imageSize.dp)
          .background(Color.LightGray)
          .border(3.dp, Palette.Primary, RectangleShape)
      )
    } else {
      Box(
        modifier = Modifier
          .size(imageSize.dp)
          .background(Color.LightGray)
          .border(3.dp, Palette.Primary, RectangleShape)
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = game.name,
      style = textStyle,
      fontSize = textSize,
      color = Palette.White,
      textAlign = TextAlign.Center,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.width(imageSize.dp)
    )
  }
}
