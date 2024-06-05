package cm.aptoide.pt.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.parseDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): Date? = try {
  SimpleDateFormat(pattern, Locale.getDefault()).parse(this)!!
} catch (t: Throwable) {
  t.printStackTrace()
  null
}

fun String.isYoutubeURL(): Boolean {
  val pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+"
  return matches(Regex(pattern))
}
