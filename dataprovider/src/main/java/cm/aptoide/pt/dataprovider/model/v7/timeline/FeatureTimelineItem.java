/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureTimelineItem implements TimelineItem<TimelineCard> {

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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $feature = this.feature;
    result = result * PRIME + ($feature == null ? 43 : $feature.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof FeatureTimelineItem)) return false;
    final FeatureTimelineItem other = (FeatureTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$feature = this.feature;
    final Object other$feature = other.feature;
    return this$feature == null ? other$feature == null : this$feature.equals(other$feature);
  }

  protected boolean canEqual(Object other) {
    return other instanceof FeatureTimelineItem;
  }
}
