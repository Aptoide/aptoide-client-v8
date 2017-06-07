package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 19/05/2017.
 */

class AggregatedSocialVideoTimelineItem implements TimelineItem<TimelineCard> {

  private AggregatedSocialVideo aggregatedSocialVideo;

  public AggregatedSocialVideoTimelineItem(
      @JsonProperty("data") AggregatedSocialVideo aggregatedSocialVideo) {
    this.aggregatedSocialVideo = aggregatedSocialVideo;
  }

  @Override public AggregatedSocialVideo getData() {
    return this.aggregatedSocialVideo;
  }
}
