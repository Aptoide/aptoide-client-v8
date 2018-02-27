package cm.aptoide.pt.view.app;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by filipegoncalves on 2/26/18.
 */

public class ListStoreAppsNavigator {

  private final FragmentNavigator fragmentNavigator;

  public ListStoreAppsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }
}
