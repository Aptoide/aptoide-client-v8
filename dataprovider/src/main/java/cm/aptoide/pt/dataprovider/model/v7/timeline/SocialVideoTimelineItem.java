package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 28/11/2016.
 */
@EqualsAndHashCode public class SocialVideoTimelineItem implements TimelineItem<TimelineCard> {

  private TimelineCard socialVideo;

  @JsonCreator public SocialVideoTimelineItem(@JsonProperty("data") SocialVideo socialVideo) {
    this.socialVideo = socialVideo;
  }

  @Override public TimelineCard getData() {
    return socialVideo;
  }
}
