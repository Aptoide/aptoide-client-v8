package cm.aptoide.pt.extensions

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

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