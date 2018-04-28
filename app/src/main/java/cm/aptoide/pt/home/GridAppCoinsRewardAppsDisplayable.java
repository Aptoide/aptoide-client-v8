package cm.aptoide.pt.home;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by filipegoncalves on 4/28/18.
 */

public class GridAppCoinsRewardAppsDisplayable extends DisplayablePojo<Application> {

  private String tag;
  private NavigationTracker navigationTracker;

  public GridAppCoinsRewardAppsDisplayable() {
  }

  public GridAppCoinsRewardAppsDisplayable(Application app, String tag,
      NavigationTracker navigationTracker) {
    super(app);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
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
}
