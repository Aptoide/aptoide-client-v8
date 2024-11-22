package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun TypingAnimation(
  dotColor: Color = Palette.Primary,
  backgroundColor: Color = Palette.Primary.copy(alpha = 0.1f),
  dotSize: Float = 12f,
  durationMillis: Int = 400,
  padding: Float = 10f,
  borderRadius: RoundedCornerShape = RoundedCornerShape(2.dp),
  dotSpacing: Float = 6f,
) {
  val numberDots = 3
  val transition = rememberInfiniteTransition(label = "")
  val dotStates = List(numberDots) { index ->
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = keyframes {
          this.durationMillis = numberDots * durationMillis
          0f at index * durationMillis
          1f at (index + 1) * durationMillis
          1f at numberDots * durationMillis
          0f at numberDots * durationMillis + 1
        },
        repeatMode = RepeatMode.Restart
      ), label = ""
    )
  }

  Surface(
    color = backgroundColor,
    shape = borderRadius,
    modifier = Modifier.padding(padding.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(padding.dp)
    ) {
      dotStates.forEachIndexed { index, animatedValue ->
        if (index > 0) {
          Spacer(modifier = Modifier.width(dotSpacing.dp))
        }

        Box(
          modifier = Modifier
            .size(dotSize.dp)
            .clip(RoundedCornerShape(2.dp))
        ) {
          // Inactive dot
          Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
          ) {
          }

          // Active dot
          Surface(
            color = dotColor,
            modifier = Modifier
              .fillMaxSize()
              .alpha(animatedValue.value)
          ) {}
        }
      }
    }
  }
}