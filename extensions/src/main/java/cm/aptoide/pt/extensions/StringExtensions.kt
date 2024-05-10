package cm.aptoide.pt.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.parseDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): Date {
  return SimpleDateFormat(pattern, Locale.getDefault()).parse(this)!!
}
