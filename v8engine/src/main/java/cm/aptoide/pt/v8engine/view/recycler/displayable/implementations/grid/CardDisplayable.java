package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.content.res.Configuration;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardDisplayable extends Displayable {

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public int getMarginWidth(Context context, int orientation) {
    if (!context.getResources().getBoolean(R.bool.is_this_a_tablet_device)) {
      return 0;
    }

    int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return (int) (width * 0.2);
    } else {
      return (int) (width * 0.1);
    }
  }

  public abstract void share(Context context);

  public abstract void like(Context context, String cardType, int rating);
}
