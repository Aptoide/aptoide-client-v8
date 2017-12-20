/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationTimelineItem implements TimelineItem<TimelineCard> {

  private final Recommendation recommendation;

  @JsonCreator
  public RecommendationTimelineItem(@JsonProperty("data") Recommendation recommendation) {
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
    if (!(o instanceof RecommendationTimelineItem)) return false;
    final RecommendationTimelineItem other = (RecommendationTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$recommendation = this.recommendation;
    final Object other$recommendation = other.recommendation;
    return this$recommendation == null ? other$recommendation == null
        : this$recommendation.equals(other$recommendation);
  }

  protected boolean canEqual(Object other) {
    return other instanceof RecommendationTimelineItem;
  }
}
