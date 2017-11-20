package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 20/12/2016.
 */
public class SocialRecommendationTimelineItem implements TimelineItem<TimelineCard> {

  private final SocialRecommendation socialRecommendation;

  @JsonCreator public SocialRecommendationTimelineItem(
      @JsonProperty("data") SocialRecommendation socialRecommendation) {
    this.socialRecommendation = socialRecommendation;
  }

  @Override public Ab getAb() {
    return this.socialRecommendation.getAb();
  }

  @Override public SocialRecommendation getData() {
    return socialRecommendation;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $socialRecommendation = this.socialRecommendation;
    result =
        result * PRIME + ($socialRecommendation == null ? 43 : $socialRecommendation.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialRecommendationTimelineItem)) return false;
    final SocialRecommendationTimelineItem other = (SocialRecommendationTimelineItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$socialRecommendation = this.socialRecommendation;
    final Object other$socialRecommendation = other.socialRecommendation;
    if (this$socialRecommendation == null ? other$socialRecommendation != null
        : !this$socialRecommendation.equals(other$socialRecommendation)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialRecommendationTimelineItem;
  }
}
