package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.settings.NewAccountFragment;

/**
 * Created by filipegoncalves on 3/26/18.
 */

public class AppsNavigator {

  private final FragmentNavigator fragmentNavigator;

  public AppsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(NewAccountFragment.newInstance(), true);
  }
}
