/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 6/17/16.
 */
@EqualsAndHashCode public class AppUpdateTimelineItem implements TimelineItem<TimelineCard> {

  private final AppUpdate appUpdate;

  @JsonCreator public AppUpdateTimelineItem(@JsonProperty("data") AppUpdate appUpdate) {
    this.appUpdate = appUpdate;
  }

  @Override public AppUpdate getData() {
    return appUpdate;
  }
}
