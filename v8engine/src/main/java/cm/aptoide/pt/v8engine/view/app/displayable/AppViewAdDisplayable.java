package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.GridAdDisplayable;

/**
 * Created by neuro on 01-08-2017.
 */
public class AppViewAdDisplayable extends GridAdDisplayable {

  public AppViewAdDisplayable() {
  }

  public AppViewAdDisplayable(MinimalAd minimalAd) {
    // TODO: 01-08-2017 neuro tags
    super(minimalAd, null);
  }

  @Override protected Configs getConfig() {
    return new Configs(3, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_ad;
  }
}
