package com.aptoide.android.aptoidegames

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle

@Composable
fun AptoideOutlinedText(
  text: String,
  style: TextStyle,
  outlineWidth: Float,
  outlineColor: Color,
  textColor: Color,
  modifier: Modifier,
) {
  Box(
    modifier = modifier
  ) {
    Text(
      text = text,
      style = style.merge(
        drawStyle = Stroke(
          width = outlineWidth,
          join = StrokeJoin.Round
        )
      ),
      color = outlineColor
    )
    Text(
      text = text,
      style = style,
      color = textColor,
    )
  }
}
