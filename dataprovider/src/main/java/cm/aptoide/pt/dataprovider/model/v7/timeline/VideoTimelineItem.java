/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode public class VideoTimelineItem implements TimelineItem<TimelineCard> {

  private final Video video;

  @JsonCreator public VideoTimelineItem(@JsonProperty("data") Video video) {
    this.video = video;
  }

  @Override public Ab getAb() {
    return this.video.getAb();
  }

  @Override public Video getData() {
    return video;
  }
}
