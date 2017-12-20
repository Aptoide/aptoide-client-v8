package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 17/07/2017.
 */

public class SimilarTimelineItem implements TimelineItem<TimelineCard> {

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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $recommendation = this.recommendation;
    result = result * PRIME + ($recommendation == null ? 43 : $recommendation.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SimilarTimelineItem)) return false;
    final SimilarTimelineItem other = (SimilarTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$recommendation = this.recommendation;
    final Object other$recommendation = other.recommendation;
    return this$recommendation == null ? other$recommendation == null
        : this$recommendation.equals(other$recommendation);
  }

  protected boolean canEqual(Object other) {
    return other instanceof SimilarTimelineItem;
  }
}
