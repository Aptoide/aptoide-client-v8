package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 19/05/2017.
 */

public class AggregatedSocialVideoTimelineItem implements TimelineItem<TimelineCard> {

  private AggregatedSocialVideo aggregatedSocialVideo;

  public AggregatedSocialVideoTimelineItem(
      @JsonProperty("data") AggregatedSocialVideo aggregatedSocialVideo) {
    this.aggregatedSocialVideo = aggregatedSocialVideo;
  }

  @Override public Ab getAb() {
    return this.aggregatedSocialVideo.getAb();
  }

  @Override public AggregatedSocialVideo getData() {
    return this.aggregatedSocialVideo;
  }
}
