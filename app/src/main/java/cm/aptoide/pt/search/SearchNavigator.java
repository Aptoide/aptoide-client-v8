package cm.aptoide.pt.search;

import androidx.fragment.app.Fragment;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.view.settings.SettingsFragment;

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

  public void navigate(SearchQueryModel searchQueryModel) {
    navigator.navigateTo(resolveFragment(searchQueryModel), true);
  }

  public SearchResultFragment resolveFragment(SearchQueryModel searchQueryModel) {
    if (storeName != null && storeName.length() > 0) {
      return SearchResultFragment.newInstance(searchQueryModel, storeName, storeTheme);
    }
    return SearchResultFragment.newInstance(searchQueryModel);
  }

  public void goToAppView(long appId, String packageName, String storeTheme, String storeName) {
    appNavigator.navigateWithStore(appId, packageName, storeTheme, storeName);
  }

  public void goToAppView(SearchAdResult searchAdResult) {
    appNavigator.navigateWithAd(searchAdResult, null);
  }

  public void goToSearchFragment(SearchQueryModel searchQueryModel) {
    final Fragment fragment = SearchResultFragment.newInstance(searchQueryModel);
    navigator.navigateTo(fragment, true);
  }

  public void goToSettings() {
    navigator.navigateTo(new SettingsFragment(), true);
  }
}
