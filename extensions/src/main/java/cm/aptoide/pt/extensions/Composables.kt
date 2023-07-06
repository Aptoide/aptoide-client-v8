package cm.aptoide.pt.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun <T> runPreviewable(
  preview: @Composable () -> T,
  real: @Composable () -> T,
): T = if (LocalInspectionMode.current) {
  preview()
} else {
  real()
}
