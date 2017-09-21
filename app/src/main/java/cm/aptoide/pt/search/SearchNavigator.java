package cm.aptoide.pt.search;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.view.navigator.FragmentNavigator;

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

  public void navigate(String query) {
    navigator.navigateTo(resolveFragment(query), true);
  }

  private Fragment resolveFragment(String query) {
    if (storeName != null && storeName.length() > 0) {
      return AptoideApplication.getFragmentProvider()
          .newSearchFragment(query, storeName);
    }

    return AptoideApplication.getFragmentProvider()
        .newSearchFragment(query);
  }
}
