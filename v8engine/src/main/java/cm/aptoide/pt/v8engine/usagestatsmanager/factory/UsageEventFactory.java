package cm.aptoide.pt.v8engine.usagestatsmanager.factory;

import android.app.usage.UsageStats;
import cm.aptoide.pt.v8engine.usagestatsmanager.UsageStatsManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 01-06-2017.
 */

public class UsageEventFactory {

  public List<UsageStatsManager.UsageEvent> fromUsageStatsList(
      List<UsageStats> filteredNewUsageStatsList) {
    List<UsageStatsManager.UsageEvent> tmp = new LinkedList<>();
    for (UsageStats usageStats : filteredNewUsageStatsList) {
      tmp.add(fromUsageStats(usageStats));
    }
    return tmp;
  }

  public UsageStatsManager.UsageEvent fromUsageStats(UsageStats usageStats) {
    return new UsageStatsManager.UsageEvent(usageStats.getPackageName(),
        usageStats.getFirstTimeStamp());
  }
}
