package cm.aptoide.pt.v8engine.social.presenter;

import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class TimelineNavigator implements TimelineNavigation {

  private final FragmentNavigator fragmentNavigator;

  public TimelineNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override
  public void navigateToAppView(long appId, String packageName, AppViewFragment.OpenType openType) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY));
  }

  @Override public void navigateToAppView(String packageName, AppViewFragment.OpenType openType) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(packageName, AppViewFragment.OpenType.OPEN_ONLY));
  }
}
