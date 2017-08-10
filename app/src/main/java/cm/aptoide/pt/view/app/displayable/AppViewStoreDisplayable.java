package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

public class AppViewStoreDisplayable extends AppViewDisplayable {

  public AppViewStoreDisplayable() {
  }

  public AppViewStoreDisplayable(GetApp getApp, AppViewAnalytics appViewAnalytics) {
    super(getApp, appViewAnalytics);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_subscription;
  }
}
