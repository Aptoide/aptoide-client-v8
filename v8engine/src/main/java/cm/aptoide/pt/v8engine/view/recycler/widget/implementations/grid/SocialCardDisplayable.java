package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CardDisplayable;

public abstract class SocialCardDisplayable extends CardDisplayable {

  public abstract void like(Context context, String cardType, int rating);

}
