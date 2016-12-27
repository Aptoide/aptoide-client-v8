package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import lombok.Getter;

public abstract class SocialCardDisplayable extends CardDisplayable {

  @Getter private final long numberOfLikes;
  @Getter private final long numberOfComments;

  SocialCardDisplayable() {
    numberOfLikes = 0;
    numberOfComments = 0;
  }

  SocialCardDisplayable(TimelineCard timelineCard, long numberOfLikes, long numberOfComments) {
    super(timelineCard);
    this.numberOfLikes = numberOfLikes;
    this.numberOfComments = numberOfComments;
  }

  public abstract void like(Context context, String cardType, int rating);
}
