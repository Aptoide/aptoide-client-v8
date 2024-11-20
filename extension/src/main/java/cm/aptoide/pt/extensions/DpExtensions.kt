package cm.aptoide.pt.extensions

import android.content.res.Resources
import androidx.compose.ui.unit.Dp

val Dp.pxValue get() = this.value * Resources.getSystem().displayMetrics.density
