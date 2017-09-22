package cm.aptoide.pt.social.leaderboard.presenter;

import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.navigator.TabNavigator;
import cm.aptoide.pt.view.store.StoreFragment;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardNavigator implements LeaderboardNavigation {

  private final FragmentNavigator fragmentNavigator;
  private final TabNavigator tabNavigator;

  public LeaderboardNavigator(FragmentNavigator fragmentNavigator, TabNavigator tabNavigator){

    this.fragmentNavigator = fragmentNavigator;
    this.tabNavigator = tabNavigator;
  }

  @Override public void navigateToUser(Long userId) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(userId, "DEFAULT", StoreFragment.OpenType.GetHome), true);

  }
}

