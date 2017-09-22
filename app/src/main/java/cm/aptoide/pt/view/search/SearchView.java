package cm.aptoide.pt.view.search;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface SearchView extends View {
  void showFollowedStoresResult();
  void showAllStoresResult();
  Observable<Integer> selectedTab();
  Observable<Long> selectedOneElementFromSearch();

  Observable<Void> noSearchLayoutSearchButtonClick();

  void showNoResultsImage();

  void setSubscribedSearchButtonHighlighted();

  void setEverywhereSearchButtonHighlighted();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  void showLoading();

  void hideLoading();

  Model getViewModel();

  void setupButtonVisibility(boolean hasSubscribedResults, boolean hasEverywhereResults);

  interface Model {

    String getCurrentQuery();

    String getStoreName();

    boolean isOnlyTrustedApps();

    int getSelectedButton();
  }
}
