package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 17/07/2017.
 */

@EqualsAndHashCode public class SimilarTimelineItem implements TimelineItem<TimelineCard> {

  private final Recommendation recommendation;

  @JsonCreator public SimilarTimelineItem(@JsonProperty("data") Recommendation recommendation) {
    this.recommendation = recommendation;
  }

  @Override public Ab getAb() {
    return this.recommendation.getAb();
  }

  @Override public Recommendation getData() {
    return recommendation;
  }
}
