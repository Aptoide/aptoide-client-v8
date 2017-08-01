/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;

/**
 * Created on 10/05/16.
 */
public class AppViewDescriptionDisplayable extends AppViewDisplayable {

  private AppViewAnalytics appViewAnalytics;

  public AppViewDescriptionDisplayable() {
  }

  public AppViewDescriptionDisplayable(GetApp getApp, AppViewAnalytics appViewAnalytics) {
    super(getApp, appViewAnalytics);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_description;
  }
}
