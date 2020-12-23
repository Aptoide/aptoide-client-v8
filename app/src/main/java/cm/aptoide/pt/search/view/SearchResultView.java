package cm.aptoide.pt.search.view;

import android.content.DialogInterface;
import android.view.MenuItem;
import androidx.core.util.Pair;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.download.view.DownloadClick;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.model.SearchResultError;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.List;
import rx.Observable;

public interface SearchResultView extends SearchSuggestionsView {

  Observable<DownloadClick> getDownloadClickEvents();

  Observable<Boolean> clickAdultContentSwitch();

  Observable<Void> retryClicked();

  void showNoResultsView();

  void showResultsView();

  void showLoading();

  void showMoreLoading();

  void hideLoading();

  void showResultsLoading();

  void addAllStoresResult(String query, List<SearchAppResult> dataList, boolean isLoadMore,
      boolean hasMore, boolean hasError, SearchResultError error);

  Model getViewModel();

  Observable<Void> searchResultsReachedBottom();

  Observable<Void> searchSetup();

  void toggleSuggestionsView();

  void toggleTrendingView();

  void hideSuggestionsViews();

  boolean isSearchViewExpanded();

  Observable<Pair<String, SearchQueryEvent>> listenToSuggestionClick();

  Observable<Void> toolbarClick();

  Observable<MenuItem> searchMenuItemClick();

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

  void showNativeAds();

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

  Observable<ScreenShotClickEvent> getScreenshotClickEvent();

  interface Model {

    SearchQueryModel getSearchQueryModel();

    String getStoreName();

    String getStoreTheme();

    boolean hasLoadedAds();

    boolean hasLoadedResults();

    List<Filter> getFilters();
  }
}
