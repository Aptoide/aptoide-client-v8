package cm.aptoide.pt.v8engine.usagestatsmanager;

import android.app.usage.UsageStats;
import android.content.Context;
import cm.aptoide.pt.v8engine.usagestatsmanager.factory.UsageEventFactory;
import cm.aptoide.pt.v8engine.usagestatsmanager.utils.CollectionUtils;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.Getter;

/**
 * Created by neuro on 01-06-2017.
 */

public class UsageStatsManager {

  private final UsageEventFactory usageEventFactory;
  private final UsageStatsHelper usageStatsHelper;

  @Getter private List<UsageEvent> usageEventsList;
  private long lastRefreshTimestamp;

  public UsageStatsManager(Context context) {
    this.usageEventsList = new LinkedList<>();
    usageStatsHelper = new UsageStatsHelper(context);
    usageEventFactory = new UsageEventFactory();
  }

  public List<UsageEvent> refresh() {
    long currentTimeMillis = System.currentTimeMillis();

    List<UsageStats> newUsageStatsList =
        usageStatsHelper.getUsageStatsList(lastRefreshTimestamp, currentTimeMillis);

    newUsageStatsList = removeUsageStatsDuplicates(newUsageStatsList);

    usageEventsList = mergeAndRemoveDuplicates(usageEventsList,
        CollectionUtils.map(newUsageStatsList, usageEventFactory::fromUsageStats));

    lastRefreshTimestamp = currentTimeMillis;
    saveTimestamp();

    return usageEventFactory.fromUsageStatsList(newUsageStatsList);
  }

  private List<UsageEvent> mergeAndRemoveDuplicates(List<UsageEvent> t1, List<UsageEvent> t2) {
    return CollectionUtils.mergeAndRemoveDuplicates(t1, t2, (t11, t21) -> t11.getPackageName()
            .equals(t21.getPackageName()),
        (lhs, rhs) -> Long.compare(lhs.getTimestamp(), rhs.getTimestamp()));
  }

  private List<UsageStats> removeUsageStatsDuplicates(List<UsageStats> list) {
    return CollectionUtils.removeDuplicates(list, (t1, t2) -> t1.getPackageName()
            .equals(t2.getPackageName()),
        (lhs, rhs) -> Long.compare(lhs.getLastTimeStamp(), rhs.getLastTimeStamp()));
  }

  public UsageStatsManager loadTimestamp() {
    if (isTimestampStored()) {
      // TODO: 02-06-2017 neuro
    }

    return this;
  }

  private boolean isTimestampStored() {
    // TODO: 02-06-2017 neuro
    return false;
  }

  private void saveTimestamp() {
    // TODO: 02-06-2017 neuro
  }

  @Data public static class UsageEvent {
    private final String packageName;
    private final long timestamp;

    public UsageEvent(String packageName, long timestamp) {
      this.packageName = packageName;
      this.timestamp = timestamp;
    }
  }
}
