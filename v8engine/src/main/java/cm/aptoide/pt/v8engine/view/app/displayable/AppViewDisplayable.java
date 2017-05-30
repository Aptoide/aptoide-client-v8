/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.app.AppViewAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Setter;
import rx.functions.Action0;

/**
 * Created on 04/05/16.
 */
@Ignore abstract class AppViewDisplayable extends DisplayablePojo<GetApp> {

  @Setter private Action0 onResumeAction;
  @Setter private Action0 onPauseAction;
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
}
