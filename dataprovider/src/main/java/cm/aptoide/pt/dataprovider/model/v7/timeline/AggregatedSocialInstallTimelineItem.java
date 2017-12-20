package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallTimelineItem implements TimelineItem<TimelineCard> {
  private final AggregatedSocialInstall aggregatedSocialInstall;

  @JsonCreator public AggregatedSocialInstallTimelineItem(
      @JsonProperty("data") AggregatedSocialInstall aggregatedSocialInstall) {
    this.aggregatedSocialInstall = aggregatedSocialInstall;
  }

  @Override public Ab getAb() {
    return this.aggregatedSocialInstall.getAb();
  }

  @Override public AggregatedSocialInstall getData() {
    return this.aggregatedSocialInstall;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $aggregatedSocialInstall = this.aggregatedSocialInstall;
    result = result * PRIME + ($aggregatedSocialInstall == null ? 43
        : $aggregatedSocialInstall.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AggregatedSocialInstallTimelineItem)) return false;
    final AggregatedSocialInstallTimelineItem other = (AggregatedSocialInstallTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$aggregatedSocialInstall = this.aggregatedSocialInstall;
    final Object other$aggregatedSocialInstall = other.aggregatedSocialInstall;
    return this$aggregatedSocialInstall == null ? other$aggregatedSocialInstall == null
        : this$aggregatedSocialInstall.equals(other$aggregatedSocialInstall);
  }

  protected boolean canEqual(Object other) {
    return other instanceof AggregatedSocialInstallTimelineItem;
  }
}
