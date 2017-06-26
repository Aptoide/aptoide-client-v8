package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 29/11/2016.
 */
@EqualsAndHashCode public class SocialStoreLatestAppsTimelineItem
    implements TimelineItem<TimelineCard> {
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
}
