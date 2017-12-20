package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 29/11/2016.
 */
public class SocialStoreLatestAppsTimelineItem implements TimelineItem<TimelineCard> {
  private SocialStoreLatestApps latestApps;

  @JsonCreator
  public SocialStoreLatestAppsTimelineItem(@JsonProperty("data") SocialStoreLatestApps latestApps) {
    this.latestApps = latestApps;
  }

  @Override public Ab getAb() {
    return this.latestApps.getAb();
  }

  @Override public SocialStoreLatestApps getData() {
    return latestApps;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $latestApps = this.latestApps;
    result = result * PRIME + ($latestApps == null ? 43 : $latestApps.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialStoreLatestAppsTimelineItem)) return false;
    final SocialStoreLatestAppsTimelineItem other = (SocialStoreLatestAppsTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$latestApps = this.latestApps;
    final Object other$latestApps = other.latestApps;
    return this$latestApps == null ? other$latestApps == null
        : this$latestApps.equals(other$latestApps);
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialStoreLatestAppsTimelineItem;
  }
}
