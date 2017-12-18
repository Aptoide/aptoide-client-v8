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

/**
 * Created on 04/05/16.
 */
public class AppViewSuggestedAppsDisplayable extends Displayable {

  private List<MinimalAd> minimalAds;
  private List<App> appsList;
  private AppViewSimilarAppAnalytics appViewSimilarAppAnalytics;
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

  public void setNavigationTracker(NavigationTracker navigationTracker) {
    this.navigationTracker = navigationTracker;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }

  public void setStoreContext(StoreContext storeContext) {
    this.storeContext = storeContext;
  }

  public List<MinimalAd> getMinimalAds() {
    return this.minimalAds;
  }

  public void setMinimalAds(List<MinimalAd> minimalAds) {
    this.minimalAds = minimalAds;
  }

  public List<App> getAppsList() {
    return this.appsList;
  }

  public void setAppsList(List<App> appsList) {
    this.appsList = appsList;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $minimalAds = this.getMinimalAds();
    result = result * PRIME + ($minimalAds == null ? 43 : $minimalAds.hashCode());
    final Object $appsList = this.getAppsList();
    result = result * PRIME + ($appsList == null ? 43 : $appsList.hashCode());
    final Object $appViewSimilarAppAnalytics = this.getAppViewSimilarAppAnalytics();
    result = result * PRIME + ($appViewSimilarAppAnalytics == null ? 43
        : $appViewSimilarAppAnalytics.hashCode());
    final Object $navigationTracker = this.getNavigationTracker();
    result = result * PRIME + ($navigationTracker == null ? 43 : $navigationTracker.hashCode());
    final Object $storeContext = this.getStoreContext();
    result = result * PRIME + ($storeContext == null ? 43 : $storeContext.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AppViewSuggestedAppsDisplayable)) return false;
    final AppViewSuggestedAppsDisplayable other = (AppViewSuggestedAppsDisplayable) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$minimalAds = this.getMinimalAds();
    final Object other$minimalAds = other.getMinimalAds();
    if (this$minimalAds == null ? other$minimalAds != null
        : !this$minimalAds.equals(other$minimalAds)) {
      return false;
    }
    final Object this$appsList = this.getAppsList();
    final Object other$appsList = other.getAppsList();
    if (this$appsList == null ? other$appsList != null : !this$appsList.equals(other$appsList)) {
      return false;
    }
    final Object this$appViewSimilarAppAnalytics = this.getAppViewSimilarAppAnalytics();
    final Object other$appViewSimilarAppAnalytics = other.getAppViewSimilarAppAnalytics();
    if (this$appViewSimilarAppAnalytics == null ? other$appViewSimilarAppAnalytics != null
        : !this$appViewSimilarAppAnalytics.equals(other$appViewSimilarAppAnalytics)) {
      return false;
    }
    final Object this$navigationTracker = this.getNavigationTracker();
    final Object other$navigationTracker = other.getNavigationTracker();
    if (this$navigationTracker == null ? other$navigationTracker != null
        : !this$navigationTracker.equals(other$navigationTracker)) {
      return false;
    }
    final Object this$storeContext = this.getStoreContext();
    final Object other$storeContext = other.getStoreContext();
    if (this$storeContext == null ? other$storeContext != null
        : !this$storeContext.equals(other$storeContext)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "AppViewSuggestedAppsDisplayable(minimalAds="
        + this.getMinimalAds()
        + ", appsList="
        + this.getAppsList()
        + ", appViewSimilarAppAnalytics="
        + this.getAppViewSimilarAppAnalytics()
        + ", navigationTracker="
        + this.getNavigationTracker()
        + ", storeContext="
        + this.getStoreContext()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof AppViewSuggestedAppsDisplayable;
  }

  public AppViewSimilarAppAnalytics getAppViewSimilarAppAnalytics() {
    return this.appViewSimilarAppAnalytics;
  }

  public void setAppViewSimilarAppAnalytics(AppViewSimilarAppAnalytics appViewSimilarAppAnalytics) {
    this.appViewSimilarAppAnalytics = appViewSimilarAppAnalytics;
  }
}
