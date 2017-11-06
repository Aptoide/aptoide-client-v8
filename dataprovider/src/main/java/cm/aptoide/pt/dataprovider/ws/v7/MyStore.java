package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;

/**
 * Created by trinkes on 12/09/2017.
 */

public class MyStore {
  private final TimelineStats timelineStats;
  private final GetHomeMeta getHomeMeta;

  public MyStore(TimelineStats timelineStats, @Nullable GetHomeMeta getHomeMeta) {

    this.timelineStats = timelineStats;
    this.getHomeMeta = getHomeMeta;
  }

  public TimelineStats getTimelineStats() {
    return timelineStats;
  }

  public GetHomeMeta getGetHomeMeta() {
    return getHomeMeta;
  }

  public boolean isCreateStore() {
    return getHomeMeta == null;
  }

  public boolean isLogged() {
    return timelineStats != null;
  }
}
