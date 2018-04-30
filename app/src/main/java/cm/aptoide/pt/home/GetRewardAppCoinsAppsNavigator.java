package cm.aptoide.pt.home;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by filipegoncalves on 4/29/18.
 */

public class GetRewardAppCoinsAppsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public GetRewardAppCoinsAppsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToRewardAppView(long appId, String packageName, String tag,
      double rewardAppCoins) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag,
            rewardAppCoins), true);
  }
}
