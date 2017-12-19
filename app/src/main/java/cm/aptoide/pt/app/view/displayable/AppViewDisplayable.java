/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import rx.functions.Action0;

/**
 * Created on 04/05/16.
 */
abstract class AppViewDisplayable extends DisplayablePojo<GetApp> {

  private Action0 onResumeAction;
  private Action0 onPauseAction;
  private AppViewAnalytics appViewAnalytics;

  public AppViewDisplayable() {

  }

  public AppViewDisplayable(GetApp getApp, AppViewAnalytics appViewAnalytics) {
    super(getApp);
    this.appViewAnalytics = appViewAnalytics;
  }

  public AppViewDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override public void onResume() {
    super.onResume();
    if (onResumeAction != null) {
      onResumeAction.call();
    }
  }

  @Override public void onPause() {
    if (onPauseAction != null) {
      onPauseAction.call();
    }
    super.onPause();
  }

  public AppViewAnalytics getAppViewAnalytics() {
    return appViewAnalytics;
  }

  public void setOnResumeAction(Action0 onResumeAction) {
    this.onResumeAction = onResumeAction;
  }

  public void setOnPauseAction(Action0 onPauseAction) {
    this.onPauseAction = onPauseAction;
  }
}
