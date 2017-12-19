/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreLatestAppsTimelineItem implements TimelineItem<TimelineCard> {

  private StoreLatestApps latestApps;

  @JsonCreator
  public StoreLatestAppsTimelineItem(@JsonProperty("data") StoreLatestApps latestApps) {
    this.latestApps = latestApps;
  }

  @Override public Ab getAb() {
    return this.latestApps.getAb();
  }

  @Override public StoreLatestApps getData() {
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
    if (!(o instanceof StoreLatestAppsTimelineItem)) return false;
    final StoreLatestAppsTimelineItem other = (StoreLatestAppsTimelineItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$latestApps = this.latestApps;
    final Object other$latestApps = other.latestApps;
    if (this$latestApps == null ? other$latestApps != null
        : !this$latestApps.equals(other$latestApps)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof StoreLatestAppsTimelineItem;
  }
}
