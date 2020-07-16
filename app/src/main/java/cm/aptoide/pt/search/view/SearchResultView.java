package cm.aptoide.pt.search.view;

import android.content.DialogInterface;
import android.view.MenuItem;
import androidx.core.util.Pair;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.search.SearchResultDiffModel;
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

  Observable<Void> clickNoResultsSearchButton();

  Observable<Boolean> clickAdultContentSwitch();

  Observable<Void> retryClicked();

  void showNoResultsView();

  void showResultsView();

  void showLoading();

  void hideLoading();

  void showResultsLoading();

  void addAllStoresResult(String query, SearchResultDiffModel dataList, boolean isLoadMore);

  Model getViewModel();

  void setAllStoresAdsEmpty();

  Observable<Void> searchResultsReachedBottom();

  void showLoadingMore();

  void hideLoadingMore();

  void setViewWithStoreNameAsSingleTab(String storeName);

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

  void showNativeAds(String query);

  void showNoNetworkView();

  void showGenericErrorView();

  void disableAdultContent();

  void enableAdultContent();

  void showAdultContentConfirmationDialog();

  Observable<DialogInterface> adultContentDialogPositiveClick();

  Observable<CharSequence> adultContentWithPinDialogPositiveClick();

  void setAdultContentSwitch(Boolean adultContent);

  void showAdultContentConfirmationDialogWithPin();

  Observable<DialogInterface> adultContentDialogNegativeClick();

  Observable<DialogInterface> adultContentPinDialogNegativeClick();

  void showWrongPinErrorMessage();

  Observable<Void> viewHasNoResults();

  Observable<List<Filter>> filtersChangeEvents();

  interface Model {

    SearchQueryModel getSearchQueryModel();

    String getStoreName();

    String getStoreTheme();

    boolean hasLoadedAds();

    void setHasLoadedAds();

    List<Filter> getFilters();

    List<SearchAppResult> getAllStoresSearchAppResults();

    List<SearchAdResult> getAllStoresSearchAdResults();

    boolean hasData();
  }
}
