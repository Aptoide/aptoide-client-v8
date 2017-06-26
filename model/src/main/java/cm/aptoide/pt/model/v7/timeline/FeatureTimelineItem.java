/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode public class FeatureTimelineItem implements TimelineItem<TimelineCard> {

  private final Feature feature;

  @JsonCreator public FeatureTimelineItem(@JsonProperty("data") Feature feature) {
    this.feature = feature;
  }

  @Override public Ab getAb() {
    return this.feature.getAb();
  }

  @Override public Feature getData() {
    return feature;
  }
}
