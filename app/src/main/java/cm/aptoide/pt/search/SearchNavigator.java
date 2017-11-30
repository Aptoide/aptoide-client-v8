package cm.aptoide.pt.search;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.OtherVersionsFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.view.StoreFragment;

public class SearchNavigator {

  private final FragmentNavigator navigator;
  private final String storeName;
  private String defaultStore;

  public SearchNavigator(FragmentNavigator navigator, String defaultStore) {
    this(navigator, "", defaultStore);
  }

  public SearchNavigator(FragmentNavigator navigator, String storeName, String defaultStore) {
    this.navigator = navigator;
    this.storeName = storeName;
    this.defaultStore = defaultStore;
  }

  public void goToOtherVersions(String name, String icon, String packageName) {
    final Fragment newOtherVersionsFragment =
        OtherVersionsFragment.newInstance(name, icon, packageName, defaultStore);
    navigator.navigateTo(newOtherVersionsFragment, true);
  }

  public void navigate(String query) {
    navigator.navigateTo(resolveFragment(query), true);
  }

  private Fragment resolveFragment(String query) {
    if (storeName != null && storeName.length() > 0) {
      return SearchResultFragment.newInstance(query, storeName);
    }
    return SearchResultFragment.newInstance(query);
  }

  public void goToAppView(long appId, String packageName, String storeTheme, String storeName) {
    final Fragment fragment =
        AppViewFragment.newInstance(appId, packageName, storeTheme, storeName);
    navigator.navigateTo(fragment, true);
  }

  public void goToAppView(SearchAdResult searchAdResult) {
    final Fragment fragment = AppViewFragment.newInstance(searchAdResult);
    navigator.navigateTo(fragment, true);
  }

  public void goToStoreFragment(String storeName, String theme) {
    final Fragment fragment = StoreFragment.newInstance(storeName, theme);
    navigator.navigateTo(fragment, true);
  }

  public void goToSearchFragment(String query, String storeName) {
    final Fragment fragment = SearchResultFragment.newInstance(query, storeName);
    navigator.navigateTo(fragment, true);
  }
}
