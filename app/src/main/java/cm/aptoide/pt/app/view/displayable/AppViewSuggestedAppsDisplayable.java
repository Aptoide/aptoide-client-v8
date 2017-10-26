/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created on 04/05/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class AppViewSuggestedAppsDisplayable
    extends Displayable {

  private List<MinimalAd> minimalAds;
  private List<App> appsList;
  @Getter private AppViewSimilarAppAnalytics appViewSimilarAppAnalytics;
  private NavigationTracker navigationTracker;
  private StoreContext storeContext;

  public AppViewSuggestedAppsDisplayable() {
  }

  public AppViewSuggestedAppsDisplayable(List<MinimalAd> minimalAds, List<App> appsList,
      AppViewSimilarAppAnalytics appViewSimilarAppAnalytics, NavigationTracker navigationTracker,
      StoreContext storeContext) {
    this.minimalAds = minimalAds;
    this.appsList = appsList;
    this.appViewSimilarAppAnalytics = appViewSimilarAppAnalytics;
    this.navigationTracker = navigationTracker;
  }

  @Override protected Displayable.Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_apps;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }
}
