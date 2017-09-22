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

  void showLoading();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();
}
