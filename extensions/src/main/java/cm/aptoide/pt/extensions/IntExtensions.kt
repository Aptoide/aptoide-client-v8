package cm.aptoide.pt.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun Int.spAsDp(): Dp = with(LocalDensity.current) { sp.toDp() }
