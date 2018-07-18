package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import java.util.AbstractMap;
import rx.Observable;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class HomeNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;
  private final AppNavigator appNavigator;

  public HomeNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator, BottomNavigationMapper bottomNavigationMapper,
      AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
    this.appNavigator = appNavigator;
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    appNavigator.navigateWithAppId(appId, packageName, NewAppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  public void navigateToRecommendsAppView(long appId, String packageName, String tag,
      HomeEvent.Type type) {
    appNavigator.navigateWithAppId(appId, packageName, parseAction(type), tag);
  }

  public void navigateWithAction(HomeEvent click) {
    fragmentNavigator.navigateTo(StoreTabGridRecyclerFragment.newInstance(click.getBundle()
        .getEvent(), click.getBundle()
        .getTitle(), "default", click.getBundle()
        .getTag(), StoreContext.home, false), true);
  }

  public void navigateToAppView(AbstractMap.SimpleEntry<String, SearchAdResult> entry) {
    appNavigator.navigateWithAdAndTag(entry.getValue(), entry.getKey());
  }

  public Observable<Integer> bottomNavigation() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(menuPosition -> bottomNavigationMapper.mapItemClicked(menuPosition)
            .equals(BottomNavigationItem.HOME));
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }

  public void navigateToRewardAppView(long appId, String packageName, String tag,
      double rewardAppCoins) {
    appNavigator.navigateWithAppcReward(appId, packageName, NewAppViewFragment.OpenType.OPEN_ONLY,
        tag, rewardAppCoins);
  }

  private NewAppViewFragment.OpenType parseAction(HomeEvent.Type type) {
    if (type.equals(HomeEvent.Type.SOCIAL_CLICK)) {
      return NewAppViewFragment.OpenType.OPEN_ONLY;
    } else if (type.equals(HomeEvent.Type.SOCIAL_INSTALL)) {
      return NewAppViewFragment.OpenType.OPEN_AND_INSTALL;
    }
    throw new IllegalStateException("TYPE " + type.name() + " NOT VALID");
  }
}
