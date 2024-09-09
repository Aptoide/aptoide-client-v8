package cm.aptoide.pt.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun Int.spAsDp(): Dp = with(LocalDensity.current) { sp.toDp() }

fun Int.formatDownloads(): String {
  val suffixes = listOf("", "K", "M", "B", "T")
  var value = this.toDouble()
  var index = 0

  while (value >= 1000 && index < suffixes.size - 1) {
    value /= 1000
    index++
  }

  return String.format(Locale.ENGLISH, "%.1f%s+", value, suffixes[index])
}
