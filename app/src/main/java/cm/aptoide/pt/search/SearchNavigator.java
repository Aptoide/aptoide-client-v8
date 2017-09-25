package cm.aptoide.pt.search;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.view.app.AppViewFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.search.SearchFragment;
import cm.aptoide.pt.view.store.StoreFragment;

public class SearchNavigator {

  private final FragmentNavigator navigator;
  private final String storeName;

  public SearchNavigator(FragmentNavigator navigator) {
    this(navigator, "");
  }

  public SearchNavigator(FragmentNavigator navigator, String storeName) {
    this.navigator = navigator;
    this.storeName = storeName;
  }

  public void goToOtherVersions(String name, String icon, String packageName) {
    final Fragment newOtherVersionsFragment = AptoideApplication.getFragmentProvider()
        .newOtherVersionsFragment(name, icon, packageName);
    navigator.navigateTo(newOtherVersionsFragment, true);
  }

  public void navigate(String query) {
    navigator.navigateTo(resolveFragment(query), true);
  }

  private Fragment resolveFragment(String query) {
    if (storeName != null && storeName.length() > 0) {
      return SearchFragment.newInstance(query, storeName);
    }
    return SearchFragment.newInstance(query);
  }

  public void goToAppView(long appId, String packageName, String storeTheme, String storeName) {
    final Fragment fragment =
        AppViewFragment.newInstance(appId, packageName, storeTheme, storeName);
    navigator.navigateTo(fragment, true);
  }

  public void goToStoreFragment(String storeName, String theme) {
    final Fragment fragment = StoreFragment.newInstance(storeName, theme);
    navigator.navigateTo(fragment, true);
  }
}
