package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 14/08/2017.
 */

public class AdTimelineItem implements TimelineItem<TimelineCard> {

  private final AdMoPub adMoPub;

  @JsonCreator public AdTimelineItem(@JsonProperty("data") AdMoPub adMoPub) {
    this.adMoPub = adMoPub;
  }

  @Override public Ab getAb() {
    return null;
  }

  @Override public AdMoPub getData() {
    return adMoPub;
  }
}
