package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BottomNavigationItem;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.settings.NewAccountFragment;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/26/18.
 */

public class AppsNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;

  public AppsNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator,
      BottomNavigationMapper bottomNavigationMapper) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(NewAccountFragment.newInstance(), true);
  }

  public Observable<Integer> bottomNavigation() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(menuPosition -> bottomNavigationMapper.mapItemClicked(menuPosition)
            .equals(BottomNavigationItem.APPS));
  }
}
