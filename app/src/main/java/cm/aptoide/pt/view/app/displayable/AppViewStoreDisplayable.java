package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.v8engine.store.StoreAnalytics;

public class AppViewStoreDisplayable extends AppViewDisplayable {

  private StoreAnalytics storeAnalytics;

  public AppViewStoreDisplayable() {
  }

  public AppViewStoreDisplayable(GetApp getApp, AppViewAnalytics appViewAnalytics,
      StoreAnalytics storeAnalytics) {
    super(getApp, appViewAnalytics);
    this.storeAnalytics = storeAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_subscription;
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }
}
