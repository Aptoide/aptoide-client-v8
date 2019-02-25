package cm.aptoide.pt.store.view.my;

import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import rx.Observable;

/**
 * Created by D01 on 21/03/18.
 */

public class MyStoresNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;

  public MyStoresNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator,
      BottomNavigationMapper bottomNavigationMapper) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
  }

  public Observable<Integer> bottomNavigationEvent() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(navigationEvent -> bottomNavigationMapper.mapItemClicked(navigationEvent)
            .equals(BottomNavigationItem.STORES));
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }
}
