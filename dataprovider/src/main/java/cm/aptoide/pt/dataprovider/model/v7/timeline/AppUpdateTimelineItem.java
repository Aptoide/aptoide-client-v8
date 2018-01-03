/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppUpdateTimelineItem implements TimelineItem<TimelineCard> {

  private final AppUpdate appUpdate;

  @JsonCreator public AppUpdateTimelineItem(@JsonProperty("data") AppUpdate appUpdate) {
    this.appUpdate = appUpdate;
  }

  @Override public Ab getAb() {
    return this.appUpdate.getAb();
  }

  @Override public AppUpdate getData() {
    return appUpdate;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $appUpdate = this.appUpdate;
    result = result * PRIME + ($appUpdate == null ? 43 : $appUpdate.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AppUpdateTimelineItem)) return false;
    final AppUpdateTimelineItem other = (AppUpdateTimelineItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$appUpdate = this.appUpdate;
    final Object other$appUpdate = other.appUpdate;
    if (this$appUpdate == null ? other$appUpdate != null
        : !this$appUpdate.equals(other$appUpdate)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof AppUpdateTimelineItem;
  }
}
