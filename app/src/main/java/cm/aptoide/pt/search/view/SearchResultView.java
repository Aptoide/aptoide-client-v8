package cm.aptoide.pt.search.view;

import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.List;
import rx.Observable;

public interface SearchResultView extends SearchSuggestionsView {

  void showFollowedStoresResult();

  void showAllStoresResult();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  Observable<String> clickNoResultsSearchButton();

  void showNoResultsView();

  void showResultsView();

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

  void setViewWithStoreNameAsSingleTab(String storeName);

  void hideFollowedStoresTab();

  void hideNonFollowedStoresTab();

  Observable<Void> searchSetup();

  void toggleSuggestionsView();

  void toggleTrendingView();

  void hideSuggestionsViews();

  boolean isSearchViewExpanded();

  Observable<SearchQueryEvent> listenToSuggestionClick();

  Observable<Void> toolbarClick();

  Observable<MenuItem> searchMenuItemClick();

  Observable<SearchAdResult> onAdClicked();

  Observable<SearchAppResult> onViewItemClicked();

  Observable<Pair<SearchAppResult, View>> onOpenPopUpMenuClicked();

  Observable<SearchViewQueryTextEvent> queryChanged();

  void queryEvent(SearchViewQueryTextEvent event);

  boolean shouldFocusInSearchBar();

  void scrollToTop();

  boolean hasResults();

  interface Model {

    List<SearchAppResult> getFollowedStoresSearchAppResults();

    List<SearchAdResult> getFollowedStoresSearchAdResults();

    String getCurrentQuery();

    String getStoreName();

    String getDefaultStoreName();

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

    List<SearchAppResult> getAllStoresSearchAppResults();

    List<SearchAdResult> getAllStoresSearchAdResults();

    boolean hasData();
  }
}
