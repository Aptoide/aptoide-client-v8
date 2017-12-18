/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessTimelineDataListResponse;

public class GetUserTimeline
    extends BaseV7EndlessTimelineDataListResponse<TimelineItem<TimelineCard>> {

  public GetUserTimeline() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetUserTimeline)) return false;
    final GetUserTimeline other = (GetUserTimeline) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetUserTimeline;
  }
}
