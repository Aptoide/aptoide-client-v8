package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by filipegoncalves on 4/29/18.
 */

public class GetRewardAppCoinsAppsNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final AppNavigator appNavigator;

  public GetRewardAppCoinsAppsNavigator(FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.appNavigator = appNavigator;
  }

  public void navigateToRewardAppView(long appId, String packageName, String tag,
      double rewardAppCoins) {
    appNavigator.navigateWithAppcReward(appId, packageName, NewAppViewFragment.OpenType.OPEN_ONLY,
        tag, rewardAppCoins);
  }
}
