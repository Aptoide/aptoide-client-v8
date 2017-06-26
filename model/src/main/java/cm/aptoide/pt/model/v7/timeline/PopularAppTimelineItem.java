package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 27/04/2017.
 */

public class PopularAppTimelineItem implements TimelineItem<TimelineCard> {
  private final PopularApp popularApp;

  @JsonCreator public PopularAppTimelineItem(@JsonProperty("data") PopularApp popularApp) {
    this.popularApp = popularApp;
  }

  @Override public Ab getAb() {
    return this.popularApp.getAb();
  }

  @Override public PopularApp getData() {
    return popularApp;
  }
}
