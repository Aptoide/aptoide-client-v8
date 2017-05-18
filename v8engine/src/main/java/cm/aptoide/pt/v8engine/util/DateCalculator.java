package cm.aptoide.pt.v8engine.util;

import android.content.Context;
import android.text.format.DateUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class DateCalculator {

  private static final long millisInADay = 1000 * 60 * 60 * 24;
  private static String mTimestampLabelYesterday;
  private static String mTimestampLabelToday;
  private static String mTimestampLabelJustNow;
  private static String mTimestampLabelMinutesAgo;
  private static String mTimestampLabelHoursAgo;
  private static String mTimestampLabelHourAgo;
  private static String mTimestampLabelDaysAgo;
  private static String mTimestampLabelWeekAgo;
  private static String mTimestampLabelWeeksAgo;
  private static String mTimestampLabelMonthAgo;
  private static String mTimestampLabelMonthsAgo;
  private static String mTimestampLabelYearAgo;
  private static String mTimestampLabelYearsAgo;
  private static String[] weekdays = new DateFormatSymbols().getWeekdays(); // get day names

  public DateCalculator() {
    mTimestampLabelYesterday = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_yesterday);
    mTimestampLabelToday = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_today);
    mTimestampLabelJustNow = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_just_now);
    mTimestampLabelMinutesAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_minutes_ago);
    mTimestampLabelHoursAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_hours_ago);
    mTimestampLabelHourAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_hour_ago);
    mTimestampLabelDaysAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_days_ago);
    mTimestampLabelWeekAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_week_ago2);
    mTimestampLabelWeeksAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_weeks_ago);
    mTimestampLabelMonthAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_month_ago);
    mTimestampLabelMonthsAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_months_ago);
    mTimestampLabelYearAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_year_ago);
    mTimestampLabelYearsAgo = V8Engine.getContext()
        .getResources()
        .getString(cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_years_ago);
  }

  private static boolean isYesterday(long date) {

    final Calendar currentDate = Calendar.getInstance();
    currentDate.setTimeInMillis(date);

    final Calendar yesterdayDate = Calendar.getInstance();
    yesterdayDate.add(Calendar.DATE, -1);

    return yesterdayDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
        && yesterdayDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
  }

  public String getTimeSinceDate(Context context, Date date) {
    if (date == null) {
      return "";
    }
    return getTimeDiffAll(context, date.getTime());
  }

  private String getTimeDiffAll(Context context, long time) {

    long diffTime = new Date().getTime() - time;

    if (isYesterday(time) || DateUtils.isToday(time)) {
      getTimeDiffString(context, time);
    } else {
      if (diffTime < DateUtils.WEEK_IN_MILLIS) {
        int diffDays = Double.valueOf(Math.ceil(diffTime / millisInADay))
            .intValue();
        return diffDays == 1 ? mTimestampLabelYesterday : AptoideUtils.StringU.getFormattedString(
            cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_days_ago, diffDays);
      } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4) {
        int diffDays = Double.valueOf(Math.ceil(diffTime / AptoideUtils.DateTimeU.WEEK_IN_MILLIS))
            .intValue();
        return diffDays == 1 ? mTimestampLabelWeekAgo : AptoideUtils.StringU.getFormattedString(
            cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_weeks_ago, diffDays);
      } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4 * 12) {
        int diffDays =
            Double.valueOf(Math.ceil(diffTime / (AptoideUtils.DateTimeU.WEEK_IN_MILLIS * 4)))
                .intValue();
        return diffDays == 1 ? mTimestampLabelMonthAgo : AptoideUtils.StringU.getFormattedString(
            cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_months_ago, diffDays);
      } else {
        int diffDays =
            Double.valueOf(Math.ceil(diffTime / (AptoideUtils.DateTimeU.WEEK_IN_MILLIS * 4 * 12)))
                .intValue();
        return diffDays == 1 ? mTimestampLabelYearAgo : AptoideUtils.StringU.getFormattedString(
            cm.aptoide.pt.utils.R.string.WidgetProvider_timestamp_years_ago, diffDays);
      }
    }

    return getTimeDiffString(context, time);
  }

  private String getTimeDiffString(Context context, long timedate) {
    Calendar startDateTime = Calendar.getInstance();
    Calendar endDateTime = Calendar.getInstance();
    endDateTime.setTimeInMillis(timedate);
    long milliseconds1 = startDateTime.getTimeInMillis();
    long milliseconds2 = endDateTime.getTimeInMillis();
    long diff = milliseconds1 - milliseconds2;

    long hours = diff / (60 * 60 * 1000);
    long minutes = diff / (60 * 1000);
    minutes = minutes - 60 * hours;
    long seconds = diff / (1000);

    boolean isToday = DateUtils.isToday(timedate);
    boolean isYesterday = isYesterday(timedate);

    if (isToday) {
      return mTimestampLabelToday;
    } else if (isYesterday) {
      return mTimestampLabelYesterday;
    } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
      return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
    } else {
      return DateUtils.formatDateTime(context, timedate, DateUtils.FORMAT_NUMERIC_DATE);
    }
  }
}
