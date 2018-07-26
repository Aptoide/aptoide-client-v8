package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.NewAppViewFragment;

/**
 * Created by filipegoncalves on 4/29/18.
 */

public class GetRewardAppCoinsAppsNavigator {
  private final AppNavigator appNavigator;

  public GetRewardAppCoinsAppsNavigator(AppNavigator appNavigator) {
    this.appNavigator = appNavigator;
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    appNavigator.navigateWithAppId(appId, packageName, NewAppViewFragment.OpenType.OPEN_ONLY, tag);
  }
}
