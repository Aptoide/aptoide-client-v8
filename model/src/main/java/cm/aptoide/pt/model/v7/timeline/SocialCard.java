package cm.aptoide.pt.model.v7.timeline;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 31/01/2017.
 */

@EqualsAndHashCode public abstract class SocialCard implements TimelineCard {

  @Getter private final List<UserTimeline> likes;
  @Getter private final My my;

  public SocialCard(List<UserTimeline> likes, My my) {
    this.likes = likes;
    this.my = my;
  }
}

