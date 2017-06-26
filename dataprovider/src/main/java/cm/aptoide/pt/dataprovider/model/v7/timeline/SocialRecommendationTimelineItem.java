package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 20/12/2016.
 */
@EqualsAndHashCode public class SocialRecommendationTimelineItem
    implements TimelineItem<TimelineCard> {

  private final SocialRecommendation socialRecommendation;

  @JsonCreator public SocialRecommendationTimelineItem(
      @JsonProperty("data") SocialRecommendation socialRecommendation) {
    this.socialRecommendation = socialRecommendation;
  }

  @Override public SocialRecommendation getData() {
    return socialRecommendation;
  }
}
