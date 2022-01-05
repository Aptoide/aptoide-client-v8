package cm.aptoide.pt.view.app;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by filipegoncalves on 2/26/18.
 */

public class ListStoreAppsNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AppNavigator appNavigator;

  public ListStoreAppsNavigator(FragmentNavigator fragmentNavigator, AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.appNavigator = appNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "");
  }
}
