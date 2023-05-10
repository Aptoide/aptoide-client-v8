package cm.aptoide.pt.aptoide_ui.textformatter

import android.content.Context
import android.text.format.DateUtils
import cm.aptoide.pt.aptoide_ui.R
import java.util.Calendar

class DateUtils private constructor() : DateUtils() {
  companion object {
    private const val millisInADay = (1000 * 60 * 60 * 24).toLong()


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
    fun getTimeDiffString(context: Context, timeDate: String): String {
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
        context.resources.getQuantityString(R.plurals.published_hours, hours.toInt(), hours)
      } else if (hours <= 0) {
        if (minutes > 0)
          context.resources.getQuantityString(R.plurals.published_minutes, minutes.toInt(), minutes)
        else
          context.getString(R.string.published_just_now)
      } else if (isToday) {
        context.getString(R.string.published_today)
      } else if (isYesterday) {
        context.getString(R.string.published_yesterday)
      } else if (startDateTime.timeInMillis - timeDateAsMilliseconds < millisInADay * 6) {
        val dayOfWeek = when (endDateTime[Calendar.DAY_OF_WEEK]) {
          Calendar.MONDAY -> R.string.published_on_monday
          Calendar.TUESDAY -> R.string.published_on_tuesday
          Calendar.WEDNESDAY -> R.string.published_on_wednesday
          Calendar.THURSDAY -> R.string.published_on_thursday
          Calendar.FRIDAY -> R.string.published_on_friday
          Calendar.SATURDAY -> R.string.published_on_saturday
          else -> R.string.published_on_sunday
        }
        context.getString(dayOfWeek)
      } else {
        context.getString(
          R.string.date_published_on,
          TextFormatter.formatDateToSystemLocale(context, timeDate)
        )
      }
    }
  }
}
