/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoTimelineItem implements TimelineItem<TimelineCard> {

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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $video = this.video;
    result = result * PRIME + ($video == null ? 43 : $video.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof VideoTimelineItem)) return false;
    final VideoTimelineItem other = (VideoTimelineItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$video = this.video;
    final Object other$video = other.video;
    if (this$video == null ? other$video != null : !this$video.equals(other$video)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof VideoTimelineItem;
  }
}
