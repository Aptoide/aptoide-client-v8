package cm.aptoide.pt.home;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class HomeNavigator {
  private final FragmentNavigator fragmentNavigator;

  public HomeNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }

  public void navigateWithAction(HomeClick click) {
    fragmentNavigator.navigateTo(StoreTabGridRecyclerFragment.newInstance(click.getBundle()
        .getEvent(), click.getBundle()
        .getTitle(), "default", click.getBundle()
        .getTag(), StoreContext.home, false), true);
  }

  public void navigateToAppView(SearchAdResult searchAdResult) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstance(searchAdResult), true);
  }
}
