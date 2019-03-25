package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import java.util.List;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/26/18.
 */

public class AppsNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;
  private final AppNavigator appNavigator;

  public AppsNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator, BottomNavigationMapper bottomNavigationMapper,
      AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
    this.appNavigator = appNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "");
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }

  public Observable<Integer> bottomNavigation() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(menuPosition -> bottomNavigationMapper.mapItemClicked(menuPosition)
            .equals(BottomNavigationItem.APPS));
  }

  public void navigateToSeeMoreAppc() {
    fragmentNavigator.navigateTo(SeeMoreAppcFragment.newInstance(), true);
  }
}
