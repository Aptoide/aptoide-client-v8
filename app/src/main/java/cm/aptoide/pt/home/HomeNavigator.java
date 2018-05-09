package cm.aptoide.pt.home;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.settings.NewAccountFragment;
import java.util.AbstractMap;
import rx.Observable;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class HomeNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;

  public HomeNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator,
      BottomNavigationMapper bottomNavigationMapper) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    fragmentNavigator.navigateTo(
        NewAppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag),
        true);
  }

  public void navigateWithAction(HomeEvent click) {
    fragmentNavigator.navigateTo(StoreTabGridRecyclerFragment.newInstance(click.getBundle()
        .getEvent(), click.getBundle()
        .getTitle(), "default", click.getBundle()
        .getTag(), StoreContext.home, false), true);
  }

  public void navigateToAppView(AbstractMap.SimpleEntry<String, SearchAdResult> entry) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstance(entry.getValue(), entry.getKey()),
        true);
  }

  public Observable<Integer> bottomNavigation() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(menuPosition -> bottomNavigationMapper.mapItemClicked(menuPosition)
            .equals(BottomNavigationItem.HOME));
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(NewAccountFragment.newInstance(), true);
  }

  public void navigateToRewardAppView(long appId, String packageName, String tag,
      double rewardAppCoins) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag,
            rewardAppCoins), true);
  }
}
