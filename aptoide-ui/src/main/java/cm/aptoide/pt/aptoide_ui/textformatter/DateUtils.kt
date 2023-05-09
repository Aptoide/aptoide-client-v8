package cm.aptoide.pt.aptoide_ui.textformatter

import android.content.Context
import android.text.format.DateUtils
import java.text.DateFormatSymbols
import java.util.*

class DateUtils private constructor() : DateUtils() {
  companion object {
    private const val millisInADay = (1000 * 60 * 60 * 24).toLong()
    private var mTimestampLabelYesterday: String = "Yesterday"
    private var mTimestampLabelToday: String = "Today"
    private var mTimestampLabelJustNow: String = "Just now"
    private val weekdays = DateFormatSymbols().weekdays // get day names


    /**
     * Checks if the given date is yesterday.
     *
     * @param date - Date to check.
     * @return TRUE if the date is yesterday, FALSE otherwise.
     */
    private fun isYesterday(date: Long): Boolean {
      val currentDate = Calendar.getInstance()
      currentDate.timeInMillis = date
      val yesterdayDate = Calendar.getInstance()
      yesterdayDate.add(Calendar.DATE, -1)
      return (yesterdayDate[Calendar.YEAR] == currentDate[Calendar.YEAR]
          && yesterdayDate[Calendar.DAY_OF_YEAR] == currentDate[Calendar.DAY_OF_YEAR])
    }

    /**
     * Displays a user-friendly date difference string
     *
     * @param timeDate Timestamp to format as date difference from now
     * @return Friendly-formatted date diff string
     */
    fun getTimeDiffString(context : Context, timeDate: String, onPrefix: Boolean = false): String {
      val timeDateAsMilliseconds = TextFormatter.parseDateToLong(timeDate)
      val startDateTime = Calendar.getInstance()
      val endDateTime = Calendar.getInstance()
      endDateTime.timeInMillis = timeDateAsMilliseconds
      val milliseconds1 = startDateTime.timeInMillis
      val milliseconds2 = endDateTime.timeInMillis
      val diff = milliseconds1 - milliseconds2
      val hours = diff / (60 * 60 * 1000)
      var minutes = diff / (60 * 1000)
      minutes -= 60 * hours
      val isToday = isToday(timeDateAsMilliseconds)
      val isYesterday = isYesterday(timeDateAsMilliseconds)
      return if (hours in 1..11) {
        if (hours == 1L) {
          return "an hour ago"
        } else {
          return "$hours hours ago"
        }
      } else if (hours <= 0) {
        if (minutes > 0) return "$minutes minutes ago" else return mTimestampLabelJustNow
      } else if (isToday) {
        mTimestampLabelToday
      } else if (isYesterday) {
        mTimestampLabelYesterday
      } else if (startDateTime.timeInMillis - timeDateAsMilliseconds < millisInADay * 6) {
        weekdays[endDateTime[Calendar.DAY_OF_WEEK]].withPreposition(onPrefix)
      } else {
        TextFormatter.formatDateToSystemLocale(context, timeDate).withPreposition(onPrefix)
      }
    }

    private fun String.withPreposition(onPrefix: Boolean) = if (onPrefix) {
      "on $this"
    } else {
      this
    }
  }
}