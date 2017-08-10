/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

/**
 * Created on 30/06/16.
 */
public class AppViewFlagThisDisplayable extends AppViewDisplayable {

  public AppViewFlagThisDisplayable() {
  }

  public AppViewFlagThisDisplayable(GetApp getApp, AppViewAnalytics appViewAnalytics) {
    super(getApp, appViewAnalytics);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_flag_this;
  }
}
