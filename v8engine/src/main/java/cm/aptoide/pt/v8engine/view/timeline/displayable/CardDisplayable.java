package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.content.res.Configuration;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.timeline.ShareCardCallback;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardDisplayable extends Displayable {

  private TimelineCard timelineCard;

  CardDisplayable() {
  }

  CardDisplayable(TimelineCard timelineCard) {
    this.timelineCard = timelineCard;
  }

  public TimelineCard getTimelineCard() {
    return timelineCard;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public int getMarginWidth(Context context, int orientation) {
    if (!context.getResources()
        .getBoolean(R.bool.is_this_a_tablet_device)) {
      return 0;
    }

    int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return (int) (width * 0.2);
    } else {
      return (int) (width * 0.1);
    }
  }

  /**
   * Shares the card and generates a corresponding Social card.
   *
   * @param shareCardCallback Listens to the result of the share operation. Pass null if you want
   * to
   * ignore it.
   */
  public abstract void share(boolean privacyResult, ShareCardCallback shareCardCallback);

  public abstract void share(ShareCardCallback shareCardCallback);

  public abstract void like(Context context, String cardType, int rating);

  public abstract void like(Context context, String cardId, String cardType, int rating);
}
