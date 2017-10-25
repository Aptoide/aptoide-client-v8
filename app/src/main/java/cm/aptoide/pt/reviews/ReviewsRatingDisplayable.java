package cm.aptoide.pt.reviews;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 04-09-2017.
 */

public class ReviewsRatingDisplayable extends Displayable {

  private GetAppMeta.App appMeta;

  public ReviewsRatingDisplayable() {
  }

  public ReviewsRatingDisplayable(GetAppMeta.App data) {
    appMeta = data;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.reviews_rating_displayable;
  }

  public GetAppMeta.App getAppMeta() {
    return appMeta;
  }
}
