package cm.aptoide.pt.v8engine.social.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.view.account.MyAccountFragment;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class TimelineNavigator implements TimelineNavigation {

  private final FragmentNavigator fragmentNavigator;
  private AptoideAccountManager accountManager;

  public TimelineNavigator(FragmentNavigator fragmentNavigator,
      AptoideAccountManager accountManager) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
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

  @Override public void navigateToStoreHome(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme));
  }

  @Override public void navigateToStoreTimeline(long userId, String storeTheme) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(userId, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToStoreTimeline(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToAddressBook() {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newAddressBookFragment());
  }

  @Override public void navigateToAccountView() {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }
}
