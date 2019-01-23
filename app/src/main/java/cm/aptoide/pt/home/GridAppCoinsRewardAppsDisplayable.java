package cm.aptoide.pt.home;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by filipegoncalves on 4/28/18.
 */

public class GridAppCoinsRewardAppsDisplayable extends DisplayablePojo<RewardApp> {

  private AppNavigator appNavigator;
  private String tag;
  private NavigationTracker navigationTracker;
  private AnalyticsManager analyticsManager;

  public GridAppCoinsRewardAppsDisplayable() {
  }

  public GridAppCoinsRewardAppsDisplayable(RewardApp app, String tag,
      NavigationTracker navigationTracker, AppNavigator appNavigator,
      AnalyticsManager analyticsManager) {
    super(app);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
    this.appNavigator = appNavigator;
    this.analyticsManager = analyticsManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_appcoins_reward_apps;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public String getTag() {
    return this.tag;
  }

  public void openAppView() {
    analyticsManager.logEvent(getPojo().getClickUrl());
    appNavigator.navigateWithDownloadUrlAndReward(getPojo().getAppId(), getPojo().getPackageName(),
        tag, getPojo().getDownloadUrl(), getPojo().getReward());
  }
}
