/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.view.app.displayable;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created on 11/05/16.
 */
public class AppViewScreenshotsDisplayable extends DisplayablePojo<GetAppMeta.App> {

  private AppViewAnalytics appViewAnalytics;

  public AppViewScreenshotsDisplayable() {
  }

  public AppViewScreenshotsDisplayable(GetAppMeta.App pojo, AppViewAnalytics appViewAnalytics) {
    super(pojo);
    this.appViewAnalytics = appViewAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_images;
  }

  public AppViewAnalytics getAppViewAnalytics() {
    return appViewAnalytics;
  }
}
