package cm.aptoide.pt.v8engine.usagestatsmanager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 3/2/15.
 */
public class UsageStatsHelper {

  public static final String TAG = UsageStatsHelper.class.getSimpleName();

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final Context context;

  public UsageStatsHelper(Context context) {
    this.context = context;
  }

  @SuppressWarnings("ResourceType") public List<UsageEvents.Event> getStats() {
    UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
    int interval = UsageStatsManager.INTERVAL_YEARLY;
    Calendar calendar = Calendar.getInstance();
    long endTime = calendar.getTimeInMillis();
    calendar.add(Calendar.YEAR, -1);
    long startTime = calendar.getTimeInMillis();

    Log.d(TAG, "Range start:" + dateFormat.format(startTime));
    Log.d(TAG, "Range end:" + dateFormat.format(endTime));

    List<UsageEvents.Event> eventList = new LinkedList<>();

    UsageEvents uEvents = usm.queryEvents(startTime, endTime);
    while (uEvents.hasNextEvent()) {
      UsageEvents.Event e = new UsageEvents.Event();
      boolean nextEventAvailable = uEvents.getNextEvent(e);

      if (nextEventAvailable) {
        eventList.add(e);
        Log.d(TAG, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp() + e.getEventType());
      }
    }

    return eventList;
  }

  public List<UsageStats> getUsageStatsList(long startTime, long endTime) {
    UsageStatsManager usm = getUsageStatsManager();

    Log.d(TAG, "Range start:" + dateFormat.format(startTime));
    Log.d(TAG, "Range end:" + dateFormat.format(endTime));

    List<UsageStats> usageStatsList =
        usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    return usageStatsList;
  }

  public void printUsageStats(List<UsageStats> usageStatsList) {
    for (android.app.usage.UsageStats u : usageStatsList) {
      Log.d(TAG,
          "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + u.getTotalTimeInForeground());
    }
  }

  public void printCurrentUsageStatus() {
    Calendar calendar = Calendar.getInstance();
    long endTime = calendar.getTimeInMillis();
    calendar.add(Calendar.YEAR, -1);
    long startTime = calendar.getTimeInMillis();

    printUsageStats(getUsageStatsList(startTime, endTime));
  }

  public void printCurrentUsageStatus(long startTime, long endTime) {
    printUsageStats(getUsageStatsList(startTime, endTime));
  }

  @SuppressWarnings("ResourceType") private UsageStatsManager getUsageStatsManager() {
    UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
    return usm;
  }
}