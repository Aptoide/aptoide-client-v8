/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created on 04/05/16.
 */
public class AppViewSuggestedAppDisplayable extends DisplayablePojo<MinimalAd> {

  private AppViewAnalytics appViewAnalytics;

  public AppViewSuggestedAppDisplayable() {
  }

  public AppViewSuggestedAppDisplayable(MinimalAd minimalAd, AppViewAnalytics appViewAnalytics) {
    super(minimalAd);
    this.appViewAnalytics = appViewAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_app;
  }

  public AppViewAnalytics getAppViewAnalytics() {
    return appViewAnalytics;
  }
}
