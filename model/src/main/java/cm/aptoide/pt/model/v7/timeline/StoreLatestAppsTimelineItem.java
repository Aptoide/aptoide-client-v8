/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode public class StoreLatestAppsTimelineItem implements TimelineItem<TimelineCard> {

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
}
