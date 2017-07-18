package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 18/05/2017.
 */

public class AggregatedSocialStoreLatestAppsTimelineItem implements TimelineItem<TimelineCard> {

  private final AggregatedSocialStoreLatestApps aggregatedSocialStoreLatestApps;

  @JsonCreator public AggregatedSocialStoreLatestAppsTimelineItem(
      @JsonProperty("data") AggregatedSocialStoreLatestApps aggregatedSocialStoreLatestApps) {
    this.aggregatedSocialStoreLatestApps = aggregatedSocialStoreLatestApps;
  }

  @Override public Ab getAb() {
    return this.aggregatedSocialStoreLatestApps.getAb();
  }

  @Override public AggregatedSocialStoreLatestApps getData() {
    return this.aggregatedSocialStoreLatestApps;
  }
}
