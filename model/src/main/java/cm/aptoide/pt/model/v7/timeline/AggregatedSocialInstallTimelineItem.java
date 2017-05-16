package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 11/05/2017.
 */

class AggregatedSocialInstallTimelineItem extends SocialInstallTimelineItem {
  private final AggregatedSocialInstall aggregatedSocialInstall;

  public AggregatedSocialInstallTimelineItem(
      @JsonProperty("data") AggregatedSocialInstall aggregatedSocialInstall) {
    super(aggregatedSocialInstall);
    this.aggregatedSocialInstall = aggregatedSocialInstall;
  }

  @Override public AggregatedSocialInstall getData() {
    return this.aggregatedSocialInstall;
  }
}
