package cm.aptoide.pt.model.v7.timeline;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 31/01/2017.
 */

@EqualsAndHashCode public abstract class SocialCard implements TimelineCard {

  @Getter private final List<UserTimeline> userLikes;

  public SocialCard(TimelineCardStats stats) {
    this.userLikes = stats.getUsersLikes();
  }
}

