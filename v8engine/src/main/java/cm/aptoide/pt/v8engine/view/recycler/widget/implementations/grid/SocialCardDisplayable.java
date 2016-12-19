package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CardDisplayable;

public abstract class SocialCardDisplayable extends CardDisplayable {

  public SocialCardDisplayable() {
  }

  public SocialCardDisplayable(TimelineCard timelineCard) {
    super(timelineCard);
  }

  public abstract void like(Context context, String cardType, int rating);
}
