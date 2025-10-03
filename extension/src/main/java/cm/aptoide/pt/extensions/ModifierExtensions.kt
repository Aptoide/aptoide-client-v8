package cm.aptoide.pt.extensions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.multiTap(
  enabled: Boolean,
  requiredTaps: Int = 5,
  tapIntervalMillis: Long = 500L,
  onTrigger: () -> Unit
): Modifier = composed {
  var tapCount by remember { mutableIntStateOf(0) }
  var lastTapTime by remember { mutableLongStateOf(0L) }

  this.then(
    Modifier.clickable(enabled = enabled) {
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastTapTime <= tapIntervalMillis) {
        tapCount++
      } else {
        tapCount = 1
      }
      lastTapTime = currentTime

      if (tapCount == requiredTaps) {
        onTrigger()
        tapCount = 0
      }
    }
  )
}

@Composable
fun Modifier.shimmerLoading(
  shimmerColor: Color,
  durationMillis: Int = 2000,
): Modifier {
  val transition = rememberInfiniteTransition(label = "")

  val translateX by transition.animateFloat(
    initialValue = 0f,
    targetValue = 500f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = durationMillis,
        easing = LinearEasing,
      ),
      repeatMode = RepeatMode.Restart,
    ),
    label = "",
  )

  return background(
    brush = Brush.linearGradient(
      colors = listOf(
        shimmerColor.copy(alpha = 0.2f),
        shimmerColor.copy(alpha = 1.0f),
        shimmerColor.copy(alpha = 0.2f),
      ),
      start = Offset(x = translateX, y = 0f),
      end = Offset(x = translateX + 200f, y = 0f),
    )
  )
}
