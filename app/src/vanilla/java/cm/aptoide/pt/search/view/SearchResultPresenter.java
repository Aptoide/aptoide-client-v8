package cm.aptoide.pt.search.view;

import android.support.annotation.NonNull;
import cm.aptoide.pt.R;
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

@SuppressWarnings({ "WeakerAccess", "Convert2MethodRef" }) public class SearchResultPresenter
    implements Presenter {
  private static final String TAG = SearchResultPresenter.class.getName();
  private final SearchResultView view;
  private final SearchAnalytics analytics;
  private final SearchNavigator navigator;
  private final CrashReport crashReport;
  private final Scheduler viewScheduler;
  private final SearchManager searchManager;
  private final String defaultStoreName;
  private final String defaultThemeName;
  private final boolean isMultiStoreSearch;
  private final TrendingManager trendingManager;
  private final SearchSuggestionManager suggestionManager;
  private final AptoideBottomNavigator bottomNavigator;
  private boolean focusInSearchBar;

  public SearchResultPresenter(SearchResultView view, SearchAnalytics analytics,
      SearchNavigator navigator, CrashReport crashReport, Scheduler viewScheduler,
      SearchManager searchManager, boolean isMultiStoreSearch, String defaultStoreName,
      String defaultThemeName,
      TrendingManager trendingManager, SearchSuggestionManager suggestionManager,
      boolean focusInSearchBar, AptoideBottomNavigator bottomNavigator) {
    this.view = view;
    this.analytics = analytics;
    this.navigator = navigator;
    this.crashReport = crashReport;
    this.viewScheduler = viewScheduler;
    this.searchManager = searchManager;
    this.isMultiStoreSearch = isMultiStoreSearch;
    this.defaultStoreName = defaultStoreName;
    this.defaultThemeName = defaultThemeName;
    this.trendingManager = trendingManager;
    this.suggestionManager = suggestionManager;
    this.focusInSearchBar = focusInSearchBar;
    this.bottomNavigator = bottomNavigator;
  }

  @Override public void present() {
    handleToolbarClick();
    handleSearchMenuItemClick();
    focusInSearchBar();
    handleSuggestionClicked();
    stopLoadingMoreOnDestroy();
    doFirstSearch();
    firstAdsDataLoad();
    handleClickFollowedStoresSearchButton();
    handleClickEverywhereSearchButton();
    handleClickToOpenAppViewFromItem();
    handleClickToOpenAppViewFromAdd();
    handleClickToOpenPopupMenu();
    handleClickOnNoResultsImage();
    handleAllStoresListReachedBottom();
    handleFollowedStoresListReachedBottom();
    handleQueryTextSubmitted();
    handleQueryTextChanged();
    handleQueryTextCleaned();
    handleClickOnBottomNav();
  }

  private void focusInSearchBar() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .filter(__ -> view.shouldFocusInSearchBar())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void stopLoadingMoreOnDestroy() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.DESTROY))
        .first()
        .toSingle()
        .observeOn(viewScheduler)
        .subscribe(__ -> view.hideLoadingMore(), e -> crashReport.log(e));
  }

  private void handleAllStoresListReachedBottom() {
    view.getLifecycle()
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

  private void handleFollowedStoresListReachedBottom() {
    view.getLifecycle()
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

  private void firstAdsDataLoad() {
    view.getLifecycle()
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

  private boolean hasValidQuery(SearchResultView.Model viewModel) {
    return viewModel.getCurrentQuery() != null && !viewModel.getCurrentQuery()
        .isEmpty();
  }

  private void handleClickToOpenAppViewFromItem() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onViewItemClicked())
        .doOnNext(data -> openAppView(data))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickToOpenAppViewFromAdd() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onAdClicked())
        .doOnNext(data -> {
          analytics.searchAppClick(view.getViewModel()
              .getCurrentQuery(), data.getPackageName());
          navigator.goToAppView(data);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickToOpenPopupMenu() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onOpenPopUpMenuClicked())
        .flatMap(pair -> {
          final SearchAppResult data = pair.first;
          final boolean hasVersions = data.hasOtherVersions();
          final String appName = data.getAppName();
          final String appIcon = data.getIcon();
          final String packageName = data.getPackageName();
          final String storeName = data.getStoreName();

          return view.showPopup(hasVersions, pair.second)
              .doOnNext(optionId -> {
                if (optionId == R.id.versions) {
                  if (isMultiStoreSearch) {
                    navigator.goToOtherVersions(appName, appIcon, packageName);
                  } else {
                    navigator.goToOtherVersions(appName, appIcon, packageName, defaultStoreName);
                  }
                } else if (optionId == R.id.go_to_store) {
                  navigator.goToStoreFragment(storeName, defaultThemeName);
                }
              });
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnNoResultsImage() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickNoResultsSearchButton())
        .filter(query -> query.length() > 1)
        .doOnNext(query -> navigator.goToSearchFragment(query, view.getViewModel()
            .getStoreName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void openAppView(SearchAppResult searchApp) {
    final String packageName = searchApp.getPackageName();
    final long appId = searchApp.getAppId();
    final String storeName = searchApp.getStoreName();
    analytics.searchAppClick(view.getViewModel()
        .getCurrentQuery(), packageName);
    navigator.goToAppView(appId, packageName, defaultThemeName, storeName);
  }

  private void handleClickFollowedStoresSearchButton() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickFollowedStoresSearchButton())
        .doOnNext(__ -> view.showFollowedStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickEverywhereSearchButton() {
    view.getLifecycle()
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
        (followedStoresCount, nonFollowedStoresCount) -> {
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
        });
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

  private void doFirstSearch() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .filter(viewModel -> viewModel.getAllStoresOffset() == 0
            && viewModel.getFollowedStoresOffset() == 0)
        .filter(viewModel -> hasValidQuery(viewModel))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .flatMapSingle(viewModel -> loadData(viewModel.getCurrentQuery(), viewModel.getStoreName(),
            viewModel.isOnlyTrustedApps()).onErrorResumeNext(err -> {
          crashReport.log(err);
          return Single.just(Integer.valueOf(0));
        })
            .observeOn(viewScheduler)
            .doOnSuccess(__2 -> view.hideLoading())
            .doOnSuccess(itemCount -> {
              if (itemCount == 0) {
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
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextCleaned() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> getDebouncedQueryChanges().filter(
            data -> !data.hasQuery() && view.isSearchViewExpanded())
            .flatMapSingle(data -> trendingManager.getTrendingListSuggestions()
                .observeOn(viewScheduler)
                .doOnSuccess(trendingList -> {
                  view.setTrendingList(trendingList);
                  view.toggleTrendingView();
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextChanged() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && !data.isSubmitted())
        .map(data -> data.getQuery()
            .toString())
        .flatMapSingle(query -> suggestionManager.getSuggestionsForApp(query)
            .onErrorResumeNext(err -> {
              if (err instanceof TimeoutException) {
                Logger.i(TAG, "Timeout reached while waiting for application suggestions");
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

  private void handleQueryTextSubmitted() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.searchSetup())
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && data.isSubmitted())
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          view.collapseSearchBar(false);
          view.hideSuggestionsViews();
          navigator.navigate(data.getQuery());
          if (data.isSuggestion()) {
            analytics.searchFromSuggestion(data.getQuery(), data.getPosition());
          } else {
            analytics.search(data.getQuery());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleSuggestionClicked() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.listenToSuggestionClick())
        .filter(data -> data.hasQuery() && data.isSubmitted())
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          view.collapseSearchBar(false);
          view.hideSuggestionsViews();
          navigator.navigate(data.getQuery());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleToolbarClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.toolbarClick())
        .doOnNext(__ -> analytics.searchStart(SearchSource.SEARCH_TOOLBAR, true))
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSearchMenuItemClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.RESUME)
        .flatMap(__ -> view.searchMenuItemClick())
        .doOnNext(__ -> analytics.searchStart(SearchSource.SEARCH_ICON, true))
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnBottomNav() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> bottomNavigator.navigationEvent())
        .filter(navigated -> view.hasResults())
        .doOnNext(__ -> view.scrollToTop())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Observable<SearchQueryEvent> getDebouncedQueryChanges() {
    return view.onQueryTextChanged()
        .debounce(250, TimeUnit.MILLISECONDS);
  }
}
