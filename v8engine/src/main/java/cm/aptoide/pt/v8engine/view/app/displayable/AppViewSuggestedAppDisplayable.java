/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.v8engine.view.app.GridAppDisplayable;
import lombok.Getter;

/**
 * Created on 04/05/16.
 */
public class AppViewSuggestedAppDisplayable extends GridAppDisplayable {

  @Getter private AppViewSimilarAppAnalytics appViewSimilarAppAnalytics;

  public AppViewSuggestedAppDisplayable() {
  }

  public AppViewSuggestedAppDisplayable(App app,
      AppViewSimilarAppAnalytics appViewSimilarAppAnalytics) {
    // TODO: 01-08-2017 neuro tags
    super(app, null, true);

    this.appViewSimilarAppAnalytics = appViewSimilarAppAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_app;
  }
}
