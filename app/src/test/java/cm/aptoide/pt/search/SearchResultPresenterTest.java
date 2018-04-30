package cm.aptoide.pt.search;

import android.support.v7.widget.SearchView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BottomNavigationItem;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.search.view.SearchResultPresenter;
import cm.aptoide.pt.search.view.SearchResultView;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 26/04/18.
 */

public class SearchResultPresenterTest {

  private static final Integer MENU_ITEM_ID_TEST = R.id.action_search;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private SearchResultPresenter presenter;
  @Mock private SearchView searchView;
  @Mock private SearchResultFragment searchResultView;
  @Mock private SearchAnalytics searchAnalytics;
  @Mock private SearchNavigator searchNavigator;
  @Mock private CrashReport crashReport;
  @Mock private SearchManager searchManager;
  @Mock private TrendingManager trendingManager;
  @Mock private SearchSuggestionManager searchSuggestionManager;
  @Mock private AptoideBottomNavigator aptoideBottomNavigator;
  @Mock private BottomNavigationMapper bottomNavigationMapper;
  @Mock private Suggestion suggestion;
  @Mock private SearchQueryEvent searchQueryEvent;
  @Mock private SearchResultView.Model searchResultModel;
  @Mock private SearchAdResult searchAdResult;
  @Mock private SearchAppResult searchAppResult;

  @Before public void setupSearchResultPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    presenter =
        new SearchResultPresenter(searchResultView, searchAnalytics, searchNavigator, crashReport,
            Schedulers.immediate(), searchManager, "", trendingManager, searchSuggestionManager,
            aptoideBottomNavigator, bottomNavigationMapper);
    //simulate view lifecycle event
    when(searchResultView.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void getTrendingOnStartTest() {
    presenter.getTrendingOnStart();

    List<Suggestion> suggestionList = new ArrayList<>();
    suggestionList.add(suggestion);
    //When the user goes to the search view
    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    //It should request a trending sugestion list
    when(trendingManager.getTrendingListSuggestions()).thenReturn(Single.just(suggestionList));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should display it
    verify(searchResultView).setTrendingList(suggestionList);
  }

  @Test public void handleToolbarClickTest() {
    presenter.handleToolbarClick();

    //When the user clicks on the search icon in the toolbar
    when(searchResultView.toolbarClick()).thenReturn(Observable.just(null));
    when(searchResultView.shouldFocusInSearchBar()).thenReturn(false);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should send the necessary analytics and focus in the searchBar
    verify(searchAnalytics).searchStart(any(), anyBoolean());
    verify(searchResultView).focusInSearchBar();
  }

  @Test public void handleSearchMenuItemClickTest() {
    presenter.handleSearchMenuItemClick();

    //When the user clicks on the search icon in the toolbar and has something already written
    when(searchResultView.searchMenuItemClick()).thenReturn(Observable.just(null));
    when(searchResultView.shouldFocusInSearchBar()).thenReturn(false);

    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //Then it should send the necessary analytics and focus in the searchBar
    verify(searchAnalytics).searchStart(any(), anyBoolean());
    verify(searchResultView).focusInSearchBar();
  }

  @Test public void focusInSearchBarTest() {
    presenter.focusInSearchBar();

    //When the user goes to the search view
    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    when(searchResultView.shouldFocusInSearchBar()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should focus on the search bar
    verify(searchResultView).focusInSearchBar();
  }

  @Test public void handleSuggestionClickedTest() {
    presenter.handleSuggestionClicked();

    //When the user clicks on a sugestion
    when(searchResultView.listenToSuggestionClick()).thenReturn(Observable.just(searchQueryEvent));
    when(searchQueryEvent.hasQuery()).thenReturn(true);
    when(searchQueryEvent.isSubmitted()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should make the search view disappear and navigate to correspondent result
    verify(searchResultView).collapseSearchBar(anyBoolean());
    verify(searchResultView).hideSuggestionsViews();
    verify(searchNavigator).navigate(anyString());
  }

  @Test public void stopLoadingMoreOnDestroyTest() {
    presenter.stopLoadingMoreOnDestroy();

    lifecycleEvent.onNext(View.LifecycleEvent.DESTROY);

    verify(searchResultView).hideLoadingMore();
  }

  @Test public void handleFragmentRestorationVisibilityTest() {
    presenter.handleFragmentRestorationVisibility();

    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    when(searchResultView.shouldFocusInSearchBar()).thenReturn(false);
    when(searchResultView.shouldShowSuggestions()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(searchResultView).setVisibilityOnRestore();
  }

  @Test public void doFirstSearchTestWithNoStoreAndNonEmptyList() {
    presenter.doFirstSearch();

    //When the user submits a search and has no store associated to the search
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getAllStoresOffset()).thenReturn(0);
    when(searchResultModel.getFollowedStoresOffset()).thenReturn(0);
    //It should load data
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchResultModel.getStoreName()).thenReturn("");
    when(searchResultModel.isOnlyTrustedApps()).thenReturn(true);
    List<SearchAppResult> searchAppResultList = new ArrayList<>();
    searchAppResultList.add(searchAppResult);

    //And search in both followed and non-followed stores
    when(searchManager.searchInFollowedStores(anyString(), anyBoolean(), anyInt())).thenReturn(
        Single.just(searchAppResultList));
    when(searchManager.searchInNonFollowedStores(anyString(), anyBoolean(), anyInt())).thenReturn(
        Single.just(searchAppResultList));
    when(searchResultModel.isAllStoresSelected()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(searchResultView).hideSuggestionsViews();
    verify(searchResultView).showLoading();

    verify(searchResultView).addFollowedStoresResult(searchAppResultList);
    verify(searchResultView).addAllStoresResult(searchAppResultList);
    verify(searchResultModel).incrementOffsetAndCheckIfReachedBottomOfFollowedStores(anyInt());
    verify(searchResultModel).incrementOffsetAndCheckIfReachedBottomOfAllStores(anyInt());
    verify(searchResultView, times(0)).hideFollowedStoresTab();
    verify(searchResultView, times(0)).hideNonFollowedStoresTab();

    verify(searchResultView).hideLoading();
    verify(searchResultView).showResultsView();
    verify(searchResultView).showAllStoresResult();
  }

  @Test public void doFirstSearchTestWithStoreAndEmptyList() {
    presenter.doFirstSearch();

    //When the user submits a search and has a store associated to the view
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getAllStoresOffset()).thenReturn(0);
    when(searchResultModel.getFollowedStoresOffset()).thenReturn(0);

    //It should load data
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchResultModel.getStoreName()).thenReturn("non-empty");
    when(searchResultModel.isOnlyTrustedApps()).thenReturn(true);
    List<SearchAppResult> searchAppResultList = new ArrayList<>();

    //And search in that specific store
    when(searchManager.searchInStore(anyString(), anyString(), anyInt())).thenReturn(
        Single.just(searchAppResultList));
    when(searchResultModel.isAllStoresSelected()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(searchResultView).hideSuggestionsViews();
    verify(searchResultView).showLoading();

    verify(searchResultView).setViewWithStoreNameAsSingleTab("non-empty");
    verify(searchResultModel).setAllStoresSelected(anyBoolean());
    verify(searchResultModel).incrementOffsetAndCheckIfReachedBottomOfFollowedStores(anyInt());

    verify(searchResultView).hideLoading();
    verify(searchResultView).showNoResultsView();
    verify(searchAnalytics).searchNoResults(anyString());
  }

  @Test public void firstAdsDataLoadTestNonEmptyAds() {
    presenter.firstAdsDataLoad();

    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchResultModel.hasLoadedAds()).thenReturn(false);
    when(searchManager.getAdsForQuery(anyString())).thenReturn(Observable.just(searchAdResult));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(searchResultModel).setHasLoadedAds();
    verify(searchResultView).setAllStoresAdsResult(searchAdResult);
    verify(searchResultView).setFollowedStoresAdsResult(searchAdResult);
  }

  @Test public void firstAdsDataLoadTestEmptyAds() {
    presenter.firstAdsDataLoad();

    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchResultModel.hasLoadedAds()).thenReturn(false);
    when(searchManager.getAdsForQuery(anyString())).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(searchResultModel).setHasLoadedAds();
    verify(searchResultView).setAllStoresAdsEmpty();
    verify(searchResultView).setFollowedStoresAdsEmpty();
  }

  @Test public void handleClickFollowedStoresSearchButtonTest() {
    presenter.handleClickFollowedStoresSearchButton();

    //When the user clicks on the followed stores tab
    when(searchResultView.clickFollowedStoresSearchButton()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should show the results from the followedStores
    verify(searchResultView).showFollowedStoresResult();
  }

  @Test public void handleClickEverywhereSearchButtonTest() {
    presenter.handleClickEverywhereSearchButton();

    //When the user clicks on the All Stores tab
    when(searchResultView.clickEverywhereSearchButton()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should display the result from all stores
    verify(searchResultView).showAllStoresResult();
  }

  @Test public void handleClickToOpenAppViewFromItemTest() {
    presenter.handleClickToOpenAppViewFromItem();

    //When the user clicks on an item from the search result list
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultView.onViewItemClicked()).thenReturn(Observable.just(searchAppResult));
    when(searchAppResult.getPackageName()).thenReturn("random");
    when(searchAppResult.getAppId()).thenReturn((long) 0);
    when(searchAppResult.getStoreName()).thenReturn("random");
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should send the necessary analytics and navigate to the app's App view
    verify(searchAnalytics).searchAppClick("non-empty", "random");
    verify(searchNavigator).goToAppView(anyLong(), eq("random"), anyString(), eq("random"));
  }

  @Test public void handleClickToOpenAppViewFromAddTest() {
    presenter.handleClickToOpenAppViewFromAdd();

    //When the user clicks on an Ad
    when(searchResultView.onAdClicked()).thenReturn(Observable.just(searchAdResult));
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchAdResult.getPackageName()).thenReturn("random");

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should send the necessary analytics and navigate to the app's App view
    verify(searchAnalytics).searchAdClick("non-empty", "random");
    verify(searchNavigator).goToAppView(searchAdResult);
  }

  @Test public void handleClickOnNoResultsImageTest() {
    presenter.handleClickOnNoResultsImage();

    //When the search has no results and the user clicks on the image from the no result view
    when(searchResultView.clickNoResultsSearchButton()).thenReturn(Observable.just("length>1"));
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.getStoreName()).thenReturn("random");

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate back to the search view
    verify(searchNavigator).goToSearchFragment(anyString(), anyString());
  }

  @Test public void handleAllStoresListReachedBottomTest() {
    presenter.handleAllStoresListReachedBottom();

    //When the user reaches the bottom of the list from the all stores tab
    when(searchResultView.allStoresResultReachedBottom()).thenReturn(Observable.just(null));
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.hasReachedBottomOfAllStores()).thenReturn(false);
    when(searchResultModel.isOnlyTrustedApps()).thenReturn(true);
    when(searchResultModel.getAllStoresOffset()).thenReturn(0);
    List<SearchAppResult> searchAppResultList = new ArrayList<>();
    searchAppResultList.add(searchAppResult);
    when(searchManager.searchInNonFollowedStores(anyString(), anyBoolean(), anyInt())).thenReturn(
        Single.just(searchAppResultList));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should display the loading more animation and load more
    verify(searchResultView).showLoadingMore();
    verify(searchResultView).addAllStoresResult(searchAppResultList);
    verify(searchResultModel).incrementOffsetAndCheckIfReachedBottomOfAllStores(anyInt());
    verify(searchResultView).hideLoadingMore();
    verify(searchResultModel).incrementOffsetAndCheckIfReachedBottomOfFollowedStores(anyInt());
  }

  @Test public void handleFollowedStoresListReachedBottomTest() {
    presenter.handleFollowedStoresListReachedBottom();

    //When the user reaches the bottom of the list from the followed stores tab
    when(searchResultView.followedStoresResultReachedBottom()).thenReturn(Observable.just(null));
    when(searchResultView.getViewModel()).thenReturn(searchResultModel);
    when(searchResultModel.hasReachedBottomOfFollowedStores()).thenReturn(false);
    when(searchResultModel.getCurrentQuery()).thenReturn("non-empty");
    when(searchResultModel.isOnlyTrustedApps()).thenReturn(true);
    when(searchResultModel.getAllStoresOffset()).thenReturn(0);
    List<SearchAppResult> searchAppResultList = new ArrayList<>();
    searchAppResultList.add(searchAppResult);
    when(searchManager.searchInFollowedStores(anyString(), anyBoolean(), anyInt())).thenReturn(
        Single.just(searchAppResultList));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should display the loading more animation and load more
    verify(searchResultView).showLoadingMore();
    verify(searchResultView).addFollowedStoresResult(searchAppResultList);
    verify(searchResultModel, times(2)).incrementOffsetAndCheckIfReachedBottomOfFollowedStores(
        anyInt());
    verify(searchResultView).hideLoadingMore();
  }

  @Test public void handleSuggestionQueryTextSubmittedTest() {
    presenter.handleQueryTextSubmitted();

    //When the user clicks on one of the suggestions
    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    when(searchResultView.onQueryTextChanged()).thenReturn(Observable.just(searchQueryEvent));
    when(searchQueryEvent.hasQuery()).thenReturn(true);
    when(searchQueryEvent.isSubmitted()).thenReturn(true);
    when(searchQueryEvent.isSuggestion()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the results and send the necessary analytics
    verify(searchResultView).collapseSearchBar(false);
    verify(searchResultView).hideSuggestionsViews();
    verify(searchNavigator).navigate(anyString());
    verify(searchAnalytics).searchFromSuggestion(anyString(), anyInt());
  }

  @Test public void handleNoSuggestionQueryTextSubmittedTest() {
    presenter.handleQueryTextSubmitted();

    //When the user submits a query and it's not a suggestion
    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    when(searchResultView.onQueryTextChanged()).thenReturn(Observable.just(searchQueryEvent));
    when(searchQueryEvent.hasQuery()).thenReturn(true);
    when(searchQueryEvent.isSubmitted()).thenReturn(true);
    when(searchQueryEvent.isSuggestion()).thenReturn(false);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the results and send the necessary analytics
    verify(searchResultView).collapseSearchBar(false);
    verify(searchResultView).hideSuggestionsViews();
    verify(searchNavigator).navigate(anyString());
    verify(searchAnalytics).search(anyString());
  }

  @Test public void handleQueryTextChangedTest() {
    presenter.handleQueryTextChanged();

    //When the query from the user changes
    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    when(searchResultView.onQueryTextChanged()).thenReturn(Observable.just(searchQueryEvent));
    when(searchQueryEvent.hasQuery()).thenReturn(true);
    when(searchQueryEvent.isSubmitted()).thenReturn(false);
    when(searchQueryEvent.getQuery()).thenReturn("non-empty");
    List<String> suggestionList = new ArrayList<>();
    suggestionList.add("non-empty");
    when(searchSuggestionManager.getSuggestionsForApp("non-empty")).thenReturn(
        Single.just(suggestionList));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should refresh the list of suggestions
    verify(searchResultView).setUnsubmittedQuery("non-empty");
    verify(searchResultView).setSuggestionsList(any());
    verify(searchResultView).toggleSuggestionsView();
  }

  @Test public void handleQueryTextCleanedTest() {
    presenter.handleQueryTextCleaned();

    //When the user clears what he/she was searching
    when(searchResultView.onQueryTextChanged()).thenReturn(Observable.just(searchQueryEvent));
    when(searchQueryEvent.hasQuery()).thenReturn(false);
    when(searchResultView.isSearchViewExpanded()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should show the trending suggestions
    verify(searchResultView).clearUnsubmittedQuery();
    verify(searchResultView).toggleTrendingView();
  }

  @Test public void handleClickOnBottomNavWithResultsTest() {
    presenter.handleClickOnBottomNavWithResults();

    //When the user clicks the BottomNavigation search item and has results
    when(aptoideBottomNavigator.navigationEvent()).thenReturn(Observable.just(MENU_ITEM_ID_TEST));
    when(bottomNavigationMapper.mapItemClicked(MENU_ITEM_ID_TEST)).thenReturn(
        BottomNavigationItem.SEARCH);
    when(searchResultView.hasResults()).thenReturn(true);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the top
    verify(searchResultView).scrollToTop();
  }

  @Test public void handleClickOnBottomNavWithoutResultsTest() {
    presenter.handleClickOnBottomNavWithoutResults();

    //When the user clicks the BottomNavigation search item and has no results
    when(aptoideBottomNavigator.navigationEvent()).thenReturn(Observable.just(MENU_ITEM_ID_TEST));
    when(bottomNavigationMapper.mapItemClicked(MENU_ITEM_ID_TEST)).thenReturn(
        BottomNavigationItem.SEARCH);
    when(searchResultView.hasResults()).thenReturn(false);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should focus in the search bar
    verify(searchResultView).focusInSearchBar();
  }

  @Test public void listenToSearchQueriesTest() {
    presenter.listenToSearchQueries();

    when(searchResultView.searchSetup()).thenReturn(Observable.just(null));
    SearchViewQueryTextEvent searchViewQueryTextEvent =
        SearchViewQueryTextEvent.create(searchView, "", true);
    when(searchResultView.queryChanged()).thenReturn(Observable.just(searchViewQueryTextEvent));

    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    verify(searchResultView).queryEvent(any());
  }
}
