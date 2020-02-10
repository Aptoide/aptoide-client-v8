package cm.aptoide.pt.search.view;

import android.view.MenuItem;
import androidx.core.util.Pair;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.List;
import rx.Observable;

public interface SearchResultView extends SearchSuggestionsView {

  void showFollowedStoresResult();

  void showAllStoresResult();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  Observable<Void> clickNoResultsSearchButton();

  Observable<Void> retryClicked();

  void showNoResultsView();

  void showResultsView();

  void showLoading();

  void hideLoading();

  void addFollowedStoresResult(String query, List<SearchAppResult> dataList);

  void addAllStoresResult(String query, List<SearchAppResult> dataList);

  Model getViewModel();

  void setFollowedStoresAdsResult(SearchAdResult ad);

  void setAllStoresAdsResult(SearchAdResult ad);

  void setFollowedStoresAdsEmpty();

  void setAllStoresAdsEmpty();

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

  Observable<Pair<String, SearchQueryEvent>> listenToSuggestionClick();

  Observable<Void> toolbarClick();

  Observable<MenuItem> searchMenuItemClick();

  Observable<SearchAdResultWrapper> onAdClicked();

  Observable<SearchAppResultWrapper> onViewItemClicked();

  Observable<SearchViewQueryTextEvent> queryChanged();

  void queryEvent(SearchViewQueryTextEvent event);

  boolean shouldFocusInSearchBar();

  void scrollToTop();

  boolean hasResults();

  void disableUpNavigation();

  boolean shouldHideUpNavigation();

  void setUnsubmittedQuery(String query);

  void clearUnsubmittedQuery();

  void setVisibilityOnRestore();

  boolean shouldShowSuggestions();

  void showBannerAd();

  Observable<Boolean> showingSearchResultsView();

  void showNativeAds(String query);

  void showNoNetworkView();

  void showGenericErrorView();

  interface Model {

    List<SearchAppResult> getFollowedStoresSearchAppResults();

    List<SearchAdResult> getFollowedStoresSearchAdResults();

    SearchQueryModel getSearchQueryModel();

    String getStoreName();

    String getStoreTheme();

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
