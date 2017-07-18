package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 11/05/2017.
 */

@EqualsAndHashCode public class AggregatedSocialInstallTimelineItem
    implements TimelineItem<TimelineCard> {
  private final AggregatedSocialInstall aggregatedSocialInstall;

  @JsonCreator public AggregatedSocialInstallTimelineItem(
      @JsonProperty("data") AggregatedSocialInstall aggregatedSocialInstall) {
    this.aggregatedSocialInstall = aggregatedSocialInstall;
  }

  @Override public Ab getAb() {
    return this.aggregatedSocialInstall.getAb();
  }

  @Override public AggregatedSocialInstall getData() {
    return this.aggregatedSocialInstall;
  }
}
