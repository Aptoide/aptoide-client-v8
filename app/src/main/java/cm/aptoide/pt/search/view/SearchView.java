package cm.aptoide.pt.search.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import java.util.List;
import rx.Observable;

public interface SearchView extends View {
  Observable<Void> clickTitleBar();

  void showFollowedStoresResult();

  void showAllStoresResult();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  Observable<String> clickNoResultsSearchButton();

  void showNoResultsView();

  void showResultsView();

  void showWidgetClickView();

  void showVoiceSearch();

  void showLoading();

  void hideLoading();

  void addFollowedStoresResult(List<SearchAppResult> dataList);

  void addAllStoresResult(List<SearchAppResult> dataList);

  Model getViewModel();

  void setFollowedStoresAdsResult(SearchAdResult ad);

  void setAllStoresAdsResult(SearchAdResult ad);

  void setFollowedStoresAdsEmpty();

  void setAllStoresAdsEmpty();

  Observable<Integer> showPopup(boolean hasVersions, android.view.View anchor);

  String getDefaultTheme();

  Observable<Void> followedStoresResultReachedBottom();

  Observable<Void> allStoresResultReachedBottom();

  void showLoadingMore();

  void hideLoadingMore();

  void setViewWithStoreNameAsSingleTab();

  void setFocusInSearchView();

  interface Model {

    String getCurrentQuery();

    String getStoreName();

    boolean isOnlyTrustedApps();

    boolean isAllStoresSelected();

    void setAllStoresSelected(boolean allStoresSelected);

    int getAllStoresOffset();

    int getFollowedStoresOffset();

    boolean hasReachedBottomOfAllStores();

    boolean hasReachedBottomOfFollowedStores();

    void incrementOffsetAndCheckIfReachedBottomOfFollowedStores(int offset);

    void incrementOffsetAndCheckIfReachedBottomOfAllStores(int offset);

    boolean hasLoadedAds();

    void setHasLoadedAds();
  }
}
