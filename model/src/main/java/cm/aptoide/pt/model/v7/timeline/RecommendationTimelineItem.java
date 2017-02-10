/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 7/8/16.
 */
@EqualsAndHashCode public class RecommendationTimelineItem implements TimelineItem<TimelineCard> {

  private final Recommendation recommendation;

  @JsonCreator
  public RecommendationTimelineItem(@JsonProperty("data") Recommendation recommendation) {
    this.recommendation = recommendation;
  }

  @Override public Recommendation getData() {
    return recommendation;
  }
}
