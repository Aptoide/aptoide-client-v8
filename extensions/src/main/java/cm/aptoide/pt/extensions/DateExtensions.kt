package cm.aptoide.pt.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(pattern: String = "dd MMMM") =
  SimpleDateFormat(pattern, Locale.getDefault()).format(this) ?: ""
