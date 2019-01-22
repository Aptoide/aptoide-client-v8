package cm.aptoide.pt.search;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.view.SearchResultFragment;

public class SearchNavigator {

  private final FragmentNavigator navigator;
  private final String storeName;
  private final String storeTheme;
  private final AppNavigator appNavigator;

  public SearchNavigator(FragmentNavigator navigator, AppNavigator appNavigator) {
    this(navigator, "", "", appNavigator);
  }

  public SearchNavigator(FragmentNavigator navigator, String storeName, String storeTheme,
      AppNavigator appNavigator) {
    this.navigator = navigator;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.appNavigator = appNavigator;
  }

  public void navigate(String query) {
    navigator.navigateTo(resolveFragment(query), true);
  }

  public SearchResultFragment resolveFragment(String query) {
    if (storeName != null && storeName.length() > 0) {
      return SearchResultFragment.newInstance(query, storeName, storeTheme);
    }
    return SearchResultFragment.newInstance(query);
  }

  public void goToAppView(long appId, String packageName, String storeTheme, String storeName) {
    appNavigator.navigateWithStore(appId, packageName, storeTheme, storeName);
  }

  public void goToAppView(SearchAdResult searchAdResult) {
    appNavigator.navigateWithAd(searchAdResult, null);
  }

  public void goToSearchFragment(String query) {
    final Fragment fragment = SearchResultFragment.newInstance(query);
    navigator.navigateTo(fragment, true);
  }
}
