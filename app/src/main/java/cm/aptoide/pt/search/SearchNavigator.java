package cm.aptoide.pt.search;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.OtherVersionsFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.view.StoreFragment;

public class SearchNavigator {

  private final FragmentNavigator navigator;
  private final String storeName;
  private final String storeTheme;
  private final String defaultStoreName;
  private final AppNavigator appNavigator;

  public SearchNavigator(FragmentNavigator navigator, String defaultStoreName,
      AppNavigator appNavigator) {
    this(navigator, "", "", defaultStoreName, appNavigator);
  }

  public SearchNavigator(FragmentNavigator navigator, String storeName, String storeTheme,
      String defaultStoreName, AppNavigator appNavigator) {
    this.navigator = navigator;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.defaultStoreName = defaultStoreName;
    this.appNavigator = appNavigator;
  }

  public void goToOtherVersions(String name, String icon, String packageName) {
    navigator.navigateTo(OtherVersionsFragment.newInstance(name, icon, packageName), true);
  }

  public void goToOtherVersions(String name, String icon, String packageName, String defaultStore) {
    navigator.navigateTo(OtherVersionsFragment.newInstance(name, icon, packageName, defaultStore),
        true);
  }

  public void navigate(String query) {
    navigator.navigateTo(resolveFragment(query), true);
  }

  public SearchResultFragment resolveFragment(String query) {
    if (storeName != null && storeName.length() > 0) {
      return SearchResultFragment.newInstance(query, storeName, storeTheme, defaultStoreName);
    }
    return SearchResultFragment.newInstance(query, defaultStoreName);
  }

  public void goToAppView(long appId, String packageName, String storeTheme, String storeName) {
    appNavigator.navigateWithStore(appId, packageName, storeTheme, storeName);
  }

  public void goToAppView(SearchAdResult searchAdResult) {
    appNavigator.navigateWithAd(searchAdResult, null);
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
