package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 15/12/2016.
 */

public class SocialInstallTimelineItem implements TimelineItem<TimelineCard> {

  private final SocialInstall socialInstall;

  @JsonCreator public SocialInstallTimelineItem(@JsonProperty("data") SocialInstall socialInstall) {
    this.socialInstall = socialInstall;
  }

  @Override public Ab getAb() {
    return this.socialInstall.getAb();
  }

  @Override public SocialInstall getData() {
    return socialInstall;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $socialInstall = this.socialInstall;
    result = result * PRIME + ($socialInstall == null ? 43 : $socialInstall.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialInstallTimelineItem)) return false;
    final SocialInstallTimelineItem other = (SocialInstallTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$socialInstall = this.socialInstall;
    final Object other$socialInstall = other.socialInstall;
    return this$socialInstall == null ? other$socialInstall == null
        : this$socialInstall.equals(other$socialInstall);
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialInstallTimelineItem;
  }
}
