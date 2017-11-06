package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 15/12/2016.
 */

@EqualsAndHashCode public class SocialInstallTimelineItem implements TimelineItem<TimelineCard> {

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
}