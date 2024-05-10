package cm.aptoide.pt.extensions

import android.icu.text.DateFormat
import java.util.Date
import java.util.Locale

fun Date.getPatternFormat(skeleton: String = "dd MMMM") =
  DateFormat.getPatternInstance(skeleton, Locale.getDefault()).format(this)!!
