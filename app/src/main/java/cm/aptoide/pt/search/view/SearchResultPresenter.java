package cm.aptoide.pt.search.view;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Func2;

@SuppressWarnings({ "WeakerAccess", "Convert2MethodRef" }) public class SearchResultPresenter
    implements Presenter {
  private static final String TAG = SearchResultPresenter.class.getName();
  private final SearchResultView view;
  private final SearchAnalytics analytics;
  private final SearchNavigator navigator;
  private final CrashReport crashReport;
  private final Scheduler viewScheduler;
  private final SearchManager searchManager;
  private final TrendingManager trendingManager;
  private final SearchSuggestionManager suggestionManager;
  private final AptoideBottomNavigator bottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;
  private final Scheduler ioScheduler;

  public SearchResultPresenter(SearchResultView view, SearchAnalytics analytics,
      SearchNavigator navigator, CrashReport crashReport, Scheduler viewScheduler,
      SearchManager searchManager, TrendingManager trendingManager,
      SearchSuggestionManager suggestionManager, AptoideBottomNavigator bottomNavigator,
      BottomNavigationMapper bottomNavigationMapper, Scheduler ioScheduler) {
    this.view = view;
    this.analytics = analytics;
    this.navigator = navigator;
    this.crashReport = crashReport;
    this.viewScheduler = viewScheduler;
    this.searchManager = searchManager;
    this.trendingManager = trendingManager;
    this.suggestionManager = suggestionManager;
    this.bottomNavigator = bottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
    this.ioScheduler = ioScheduler;
  }

  @Override public void present() {
    getTrendingOnStart();
    handleToolbarClick();
    handleSearchMenuItemClick();
    focusInSearchBar();
    handleSuggestionClicked();
    stopLoadingMoreOnDestroy();
    handleFragmentRestorationVisibility();
    doFirstSearch();
    firstAdsDataLoad();
    handleClickFollowedStoresSearchButton();
    handleClickEverywhereSearchButton();
    handleClickToOpenAppViewFromItem();
    handleClickToOpenAppViewFromAdd();
    handleClickOnNoResultsImage();
    handleAllStoresListReachedBottom();
    handleFollowedStoresListReachedBottom();
    handleQueryTextSubmitted();
    handleQueryTextChanged();
    handleQueryTextCleaned();
    handleClickOnBottomNavWithResults();
    handleClickOnBottomNavWithoutResults();
    listenToSearchQueries();

    loadBannerAd();
  }

  private void loadBannerAd() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.showingSearchResultsView())
        .observeOn(ioScheduler)
        .flatMapSingle(__ -> searchManager.shouldLoadBannerAd())
        .filter(loadBanner -> loadBanner)
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showBannerAd())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleFragmentRestorationVisibility() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .filter(__ -> !view.shouldFocusInSearchBar() && view.shouldShowSuggestions())
        .doOnNext(__ -> view.setVisibilityOnRestore())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void getTrendingOnStart() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .flatMapSingle(__ -> trendingManager.getTrendingListSuggestions()
            .observeOn(viewScheduler)
            .doOnSuccess(trending -> view.setTrendingList(trending)))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void focusInSearchBar() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .first()
        .filter(__ -> view.shouldFocusInSearchBar())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void stopLoadingMoreOnDestroy() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.DESTROY))
        .first()
        .toSingle()
        .observeOn(viewScheduler)
        .subscribe(__ -> view.hideLoadingMore(), e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleAllStoresListReachedBottom() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.allStoresResultReachedBottom())
        .map(__ -> view.getViewModel())
        .filter(viewModel -> !viewModel.hasReachedBottomOfAllStores())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoadingMore())
        .flatMapSingle(viewModel -> loadDataFromNonFollowedStores(viewModel.getCurrentQuery(),
            viewModel.isOnlyTrustedApps(), viewModel.getAllStoresOffset()).onErrorResumeNext(
            err -> {
              crashReport.log(err);
              return Single.just(null);
            }))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideLoadingMore())
        .filter(data -> data != null)
        .doOnNext(data -> {
          final SearchResultView.Model viewModel = view.getViewModel();
          viewModel.incrementOffsetAndCheckIfReachedBottomOfFollowedStores(getItemCount(data));
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private int getItemCount(List<SearchAppResult> data) {
    return data != null ? data.size() : 0;
  }

  @VisibleForTesting public void handleFollowedStoresListReachedBottom() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.followedStoresResultReachedBottom())
        .map(__ -> view.getViewModel())
        .filter(viewModel -> !viewModel.hasReachedBottomOfFollowedStores())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoadingMore())
        .flatMapSingle(viewModel -> loadDataFromFollowedStores(viewModel.getCurrentQuery(),
            viewModel.isOnlyTrustedApps(), viewModel.getFollowedStoresOffset()).onErrorResumeNext(
            err -> {
              crashReport.log(err);
              return Single.just(null);
            }))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideLoadingMore())
        .filter(data -> data != null)
        .doOnNext(data -> {
          final SearchResultView.Model viewModel = view.getViewModel();
          viewModel.incrementOffsetAndCheckIfReachedBottomOfFollowedStores(getItemCount(data));
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void firstAdsDataLoad() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .filter(viewModel -> hasValidQuery(viewModel))
        .filter(viewModel -> !viewModel.hasLoadedAds())
        .flatMap(viewModel -> searchManager.getAdsForQuery(viewModel.getCurrentQuery())
            .onErrorReturn(err -> {
              crashReport.log(err);
              return null;
            })
            .observeOn(viewScheduler)
            .doOnNext(__ -> viewModel.setHasLoadedAds())
            .doOnNext(ad -> {
              if (ad == null) {
                view.setFollowedStoresAdsEmpty();
                view.setAllStoresAdsEmpty();
              } else {
                view.setAllStoresAdsResult(ad);
                view.setFollowedStoresAdsResult(ad);
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public boolean hasValidQuery(SearchResultView.Model viewModel) {
    return viewModel.getCurrentQuery() != null && !viewModel.getCurrentQuery()
        .isEmpty();
  }

  @VisibleForTesting public void handleClickToOpenAppViewFromItem() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onViewItemClicked())
        .observeOn(ioScheduler)
        .flatMap(data -> searchManager.recordAction()
            .doOnNext(__ -> openAppView(data)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleClickToOpenAppViewFromAdd() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onAdClicked())
        .doOnNext(data -> {
          analytics.searchAdClick(view.getViewModel()
              .getCurrentQuery(), data.getSearchAdResult()
              .getPackageName(), data.getPosition());
          navigator.goToAppView(data.getSearchAdResult());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleClickOnNoResultsImage() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickNoResultsSearchButton())
        .filter(query -> query.length() > 1)
        .doOnNext(query -> navigator.goToSearchFragment(query))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void openAppView(SearchAppResultWrapper searchApp) {
    final String packageName = searchApp.getSearchAppResult()
        .getPackageName();
    final long appId = searchApp.getSearchAppResult()
        .getAppId();
    final String storeName = searchApp.getSearchAppResult()
        .getStoreName();
    analytics.searchAppClick(view.getViewModel()
        .getCurrentQuery(), packageName, searchApp.getPosition());
    navigator.goToAppView(appId, packageName, view.getViewModel()
        .getStoreTheme(), storeName);
  }

  @VisibleForTesting public void handleClickFollowedStoresSearchButton() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickFollowedStoresSearchButton())
        .doOnNext(__ -> view.showFollowedStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleClickEverywhereSearchButton() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickEverywhereSearchButton())
        .doOnNext(__ -> view.showAllStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private Single<Integer> loadData(String query, String storeName, boolean onlyTrustedApps) {

    if (storeName != null && !storeName.trim()
        .equals("")) {
      return Completable.fromAction(() -> view.setViewWithStoreNameAsSingleTab(storeName))
          .andThen(loadDataForSpecificStore(query, storeName, 0).map(
              list -> list != null ? list.size() : 0));
    }
    // search every store. followed and not followed
    return Single.zip(loadDataFromFollowedStores(query, onlyTrustedApps, 0),
        loadDataFromNonFollowedStores(query, onlyTrustedApps, 0),
        new Func2<List<SearchAppResult>, List<SearchAppResult>, Integer>() {
          @Override public Integer call(List<SearchAppResult> followedStoresCount,
              List<SearchAppResult> nonFollowedStoresCount) {
            int result = 0;
            if (followedStoresCount != null && followedStoresCount.size() > 0) {
              result += followedStoresCount.size();
            } else {
              view.hideFollowedStoresTab();
            }

            if (nonFollowedStoresCount != null && nonFollowedStoresCount.size() > 0) {
              result += nonFollowedStoresCount.size();
            } else {
              view.hideNonFollowedStoresTab();
            }

            return result;
          }
        });
  }

  @NonNull private Single<List<SearchAppResult>> loadDataFromNonFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return searchManager.searchInNonFollowedStores(query, onlyTrustedApps, offset)
        .observeOn(viewScheduler)
        .doOnSuccess(dataList -> view.addAllStoresResult(dataList))
        .doOnSuccess(data -> {
          final SearchResultView.Model viewModel = view.getViewModel();
          viewModel.incrementOffsetAndCheckIfReachedBottomOfAllStores(getItemCount(data));
        })
        .observeOn(ioScheduler)
        .flatMap(nonFollowedStoresSearchResult -> searchManager.shouldLoadNativeAds()
            .observeOn(viewScheduler)
            .doOnSuccess(loadNativeAds -> {
              if (loadNativeAds) {
                view.showNativeAds(query);
              }
            })
            .map(__ -> nonFollowedStoresSearchResult));
  }

  @NonNull private Single<List<SearchAppResult>> loadDataFromFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return searchManager.searchInFollowedStores(query, onlyTrustedApps, offset)
        .observeOn(viewScheduler)
        .doOnSuccess(dataList -> view.addFollowedStoresResult(dataList))
        .doOnSuccess(data -> {
          final SearchResultView.Model viewModel = view.getViewModel();
          viewModel.incrementOffsetAndCheckIfReachedBottomOfFollowedStores(getItemCount(data));
        });
  }

  @NonNull
  private Single<List<SearchAppResult>> loadDataForSpecificStore(String query, String storeName,
      int offset) {
    return searchManager.searchInStore(query, storeName, offset)
        .observeOn(viewScheduler)
        .doOnSuccess(dataList -> view.addFollowedStoresResult(dataList))
        .doOnSuccess(data -> {
          final SearchResultView.Model viewModel = view.getViewModel();
          viewModel.setAllStoresSelected(false);
          viewModel.incrementOffsetAndCheckIfReachedBottomOfFollowedStores(getItemCount(data));
        });
  }

  @VisibleForTesting public void doFirstSearch() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .filter(viewModel -> viewModel.getAllStoresOffset() == 0
            && viewModel.getFollowedStoresOffset() == 0)
        .filter(viewModel -> hasValidQuery(viewModel))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideSuggestionsViews())
        .doOnNext(__ -> view.showLoading())
        .observeOn(ioScheduler)
        .flatMapSingle(viewModel -> loadData(viewModel.getCurrentQuery(), viewModel.getStoreName(),
            viewModel.isOnlyTrustedApps()).onErrorResumeNext(err -> {
          crashReport.log(err);
          return Single.just(-1);
        })
            .observeOn(viewScheduler)
            .doOnSuccess(__2 -> view.hideLoading())
            .doOnSuccess(itemCount -> {
              if (itemCount <= 0) {
                view.showNoResultsView();
                analytics.searchNoResults(viewModel.getCurrentQuery());
              } else {
                view.showResultsView();
                if (viewModel.isAllStoresSelected()) {
                  view.showAllStoresResult();
                } else {
                  view.showFollowedStoresResult();
                }
              }
            }))
        .filter(result -> result != -1)
        .observeOn(ioScheduler)
        .flatMap(__ -> searchManager.recordImpression())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleQueryTextCleaned() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> getDebouncedQueryChanges().filter(
            data -> !data.hasQuery() && view.isSearchViewExpanded())
            .observeOn(viewScheduler)
            .doOnNext(data -> {
              view.clearUnsubmittedQuery();
              view.toggleTrendingView();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleQueryTextChanged() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .first()
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && !data.isSubmitted())
        .map(data -> data.getQuery())
        .doOnNext(query -> view.setUnsubmittedQuery(query))
        .flatMapSingle(query -> suggestionManager.getSuggestionsForApp(query)
            .onErrorResumeNext(err -> {
              if (err instanceof TimeoutException) {
                Logger.getInstance()
                    .i(TAG, "Timeout reached while waiting for application suggestions");
              }
              return Single.error(err);
            })
            .observeOn(viewScheduler)
            .doOnSuccess(queryResults -> {
              view.setSuggestionsList(queryResults);
              view.toggleSuggestionsView();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleQueryTextSubmitted() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .first()
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && data.isSubmitted())
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          view.collapseSearchBar(false);
          view.hideSuggestionsViews();
          analytics.search(data.getQuery());
          navigator.navigate(data.getQuery());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleSuggestionClicked() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.listenToSuggestionClick())
        .filter(data -> data.second.hasQuery() && data.second.isSubmitted())
        .doOnNext(data -> {
          view.collapseSearchBar(false);
          view.hideSuggestionsViews();
          navigator.navigate(data.second.getQuery());
          analytics.searchFromSuggestion(data.second.getQuery(), data.second.getPosition(),
              data.first);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public void handleToolbarClick() {
    view.getLifecycleEvent()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.toolbarClick())
        .doOnNext(__ -> {
          if (!view.shouldFocusInSearchBar()) {
            analytics.searchStart(SearchSource.SEARCH_TOOLBAR, true);
          }
        })
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @VisibleForTesting public void handleSearchMenuItemClick() {
    view.getLifecycleEvent()
        .filter(event -> event == View.LifecycleEvent.RESUME)
        .flatMap(__ -> view.searchMenuItemClick())
        .doOnNext(__ -> {
          if (!view.shouldFocusInSearchBar()) analytics.searchStart(SearchSource.SEARCH_ICON, true);
        })
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @VisibleForTesting public void handleClickOnBottomNavWithResults() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> bottomNavigator.navigationEvent()
            .filter(navigationEvent -> bottomNavigationMapper.mapItemClicked(navigationEvent)
                .equals(BottomNavigationItem.SEARCH))
            .observeOn(viewScheduler)
            .filter(navigated -> view.hasResults())
            .doOnNext(__ -> view.scrollToTop())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleClickOnBottomNavWithoutResults() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> bottomNavigator.navigationEvent()
            .filter(navigationEvent -> bottomNavigationMapper.mapItemClicked(navigationEvent)
                .equals(BottomNavigationItem.SEARCH))
            .observeOn(viewScheduler)
            .filter(navigated -> !view.hasResults())
            .doOnNext(__ -> view.focusInSearchBar())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void listenToSearchQueries() {
    view.getLifecycleEvent()
        .filter(event -> event == View.LifecycleEvent.RESUME)
        .flatMap(__ -> view.searchSetup())
        .first()
        .flatMap(__ -> view.queryChanged())
        .doOnNext(event -> view.queryEvent(event))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @NonNull private Observable<SearchQueryEvent> getDebouncedQueryChanges() {
    return view.onQueryTextChanged()
        .debounce(250, TimeUnit.MILLISECONDS);
  }
}
