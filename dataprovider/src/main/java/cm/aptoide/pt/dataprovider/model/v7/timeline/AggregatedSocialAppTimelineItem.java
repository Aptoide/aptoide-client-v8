package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by analara on 14/09/2017.
 */

public class AggregatedSocialAppTimelineItem extends AggregatedSocialInstallTimelineItem {
  public AggregatedSocialAppTimelineItem(
      @JsonProperty("data") AggregatedSocialInstall aggregatedSocialInstall) {
    super(aggregatedSocialInstall);
  }
}
