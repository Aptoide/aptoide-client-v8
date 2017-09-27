/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.app.view.GridAppDisplayable;
import lombok.Getter;

/**
 * Created on 04/05/16.
 */
public class AppViewSuggestedAppDisplayable extends GridAppDisplayable {

  @Getter private AppViewSimilarAppAnalytics appViewSimilarAppAnalytics;

  public AppViewSuggestedAppDisplayable() {
  }

  public AppViewSuggestedAppDisplayable(App app,
      AppViewSimilarAppAnalytics appViewSimilarAppAnalytics,
      AptoideNavigationTracker aptoideNavigationTracker, StoreContext storeContext) {
    // TODO: 01-08-2017 neuro tags
    super(app, null, true, aptoideNavigationTracker, storeContext);

    this.appViewSimilarAppAnalytics = appViewSimilarAppAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_app;
  }
}
