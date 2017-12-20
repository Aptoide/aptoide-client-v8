package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideoTimelineItem implements TimelineItem<TimelineCard> {

  private SocialVideo socialVideo;

  @JsonCreator public SocialVideoTimelineItem(@JsonProperty("data") SocialVideo socialVideo) {
    this.socialVideo = socialVideo;
  }

  @Override public Ab getAb() {
    return this.socialVideo.getAb();
  }

  @Override public SocialVideo getData() {
    return socialVideo;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $socialVideo = this.socialVideo;
    result = result * PRIME + ($socialVideo == null ? 43 : $socialVideo.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialVideoTimelineItem)) return false;
    final SocialVideoTimelineItem other = (SocialVideoTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$socialVideo = this.socialVideo;
    final Object other$socialVideo = other.socialVideo;
    return this$socialVideo == null ? other$socialVideo == null
        : this$socialVideo.equals(other$socialVideo);
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialVideoTimelineItem;
  }
}
