package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun PaEProgressIndicator(modifier: Modifier = Modifier, progress: Float) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(8.dp)
      .background(Palette.Grey)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(progress)
        .fillMaxHeight()
        .background(color = Color.White)
        .background(
          brush = Brush.horizontalGradient(
            *arrayOf(
              0.8f to Palette.Yellow100,
              1f to Palette.Yellow100.copy(alpha = 0.2f)
            )
          )
        )
    )
  }
}

@Preview
@Composable
fun ProgressIndicatorPreview() {
  PaEProgressIndicator(
    progress = Random.nextInt(0, 1).toFloat()
  )
}
