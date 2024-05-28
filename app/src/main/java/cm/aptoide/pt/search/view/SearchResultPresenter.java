package cm.aptoide.pt.search.view;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.download.view.DownloadViewActionPresenter;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.model.Source;
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
import rx.schedulers.Schedulers;

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

  private final DownloadViewActionPresenter downloadActionPresenter;

  public SearchResultPresenter(SearchResultView view, SearchAnalytics analytics,
      SearchNavigator navigator, CrashReport crashReport, Scheduler viewScheduler,
      SearchManager searchManager, TrendingManager trendingManager,
      SearchSuggestionManager suggestionManager, AptoideBottomNavigator bottomNavigator,
      BottomNavigationMapper bottomNavigationMapper, Scheduler ioScheduler,
      DownloadViewActionPresenter downloadActionPresenter) {
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
    this.downloadActionPresenter = downloadActionPresenter;
  }

  @Override public void present() {
    handleNewSearchResults();
    getTrendingOnStart();
    handleToolbarClick();
    handleSearchMenuItemClick();
    focusInSearchBar();
    handleSuggestionClicked();
    handleFragmentRestorationVisibility();
    doFirstSearch();
    handleClickToOpenAppViewFromItem();
    handleSearchListReachedBottom();
    handleQueryTextSubmitted();
    handleQueryTextChanged();
    handleQueryTextCleaned();
    handleClickOnBottomNavWithResults();
    handleClickOnBottomNavWithoutResults();
    handleErrorRetryClick();
    handleFiltersClick();
    handleClickOnScreenshot();
    listenToSearchQueries();

    handleClickOnAdultContentSwitch();
    handleAdultContentDialogPositiveClick();
    handleAdultContentDialogNegativeClick();
    handleAdultContentDialogWithPinPositiveClick();
    redoSearchAfterAdultContentSwitch();
    updateAdultContentSwitchOnNoResults();

    downloadActionPresenter.setContextParams(DownloadAnalytics.AppContext.SEARCH, false, null);
    downloadActionPresenter.present(view.getDownloadClickEvents(), view);
  }

  private void handleNewSearchResults() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .flatMap(viewModel -> searchManager.observeSearchResults()
            .observeOn(viewScheduler)
            .doOnNext(
                result -> view.addAllStoresResult(result.getQuery(), result.getSearchResultsList(),
                    result.isFreshResult(), result.hasMore(), result.hasError(), result.getError()))
            .doOnNext(searchResult -> {
              if (!searchResult.hasError()
                  && searchResult.getSearchResultsList()
                  .size() == 0) {
                analytics.searchNoResults(viewModel.getSearchQueryModel());
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void handleFiltersClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.filtersChangeEvents()
            .map(___ -> view.getViewModel())
            .doOnNext(___ -> view.showResultsLoading())
            .flatMapCompletable(viewModel -> loadData(viewModel.getSearchQueryModel()
                .getFinalQuery(), viewModel.getStoreName(), viewModel.getFilters(), false))
            .retry())
        .doOnNext(filters -> view.getViewModel())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void handleClickOnScreenshot() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.getScreenshotClickEvent())
        .filter(event -> !event.isVideo())
        .doOnNext(imageClick -> {
          navigator.navigateToScreenshots(imageClick.getImagesUris(), imageClick.getImagesIndex());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleErrorRetryClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked())
        .observeOn(viewScheduler)
        .map(__ -> view.getViewModel())
        .flatMapCompletable(model -> search(model, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
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

  @VisibleForTesting public void handleSearchListReachedBottom() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.searchResultsReachedBottom())
        .map(__ -> view.getViewModel())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showMoreLoading())
        .flatMapCompletable(viewModel -> loadData(viewModel.getSearchQueryModel()
            .getFinalQuery(), viewModel.getStoreName(), viewModel.getFilters(), false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @VisibleForTesting public boolean hasValidQuery(SearchResultView.Model viewModel) {
    return viewModel.getSearchQueryModel() != null && !viewModel.getSearchQueryModel()
        .getFinalQuery()
        .isEmpty();
  }

  @VisibleForTesting public void handleClickToOpenAppViewFromItem() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.onViewItemClicked())
        .doOnNext(data -> openAppView(data))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  public void handleClickOnAdultContentSwitch() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickAdultContentSwitch())
        .observeOn(Schedulers.io())
        .flatMap(isChecked -> {
          if (!isChecked) {
            return searchManager.disableAdultContent()
                .observeOn(viewScheduler)
                .doOnError(e -> view.enableAdultContent())
                .toObservable()
                .map(__ -> false);
          } else {
            return Observable.just(true);
          }
        })
        .observeOn(viewScheduler)
        .filter(show -> show)
        .flatMap(__ -> searchManager.isAdultContentPinRequired())
        .doOnNext(pinRequired -> {
          if (pinRequired) {
            view.showAdultContentConfirmationDialogWithPin();
          } else {
            view.showAdultContentConfirmationDialog();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleAdultContentDialogPositiveClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.adultContentDialogPositiveClick())
        .observeOn(Schedulers.io())
        .flatMapCompletable(click -> searchManager.enableAdultContent())
        .observeOn(viewScheduler)
        .doOnError(e -> view.disableAdultContent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleAdultContentDialogNegativeClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.adultContentPinDialogNegativeClick(),
            view.adultContentDialogNegativeClick()))
        .doOnNext(__ -> view.disableAdultContent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleAdultContentDialogWithPinPositiveClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.adultContentWithPinDialogPositiveClick()
            .observeOn(Schedulers.io())
            .flatMap(pin -> searchManager.enableAdultContentWithPin(pin.toString()
                    .isEmpty() ? 0 : Integer.valueOf(pin.toString()))
                .toObservable()
                .observeOn(viewScheduler)
                .doOnError(throwable -> {
                  if (throwable instanceof SecurityException) {
                    view.showWrongPinErrorMessage();
                  }
                }))
            .retry())
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
    final boolean isMigration = (searchApp.getSearchAppResult().getDownloadModel() != null
        && searchApp.getSearchAppResult().getDownloadModel().getAction()
        == DownloadStatusModel.Action.MIGRATE);
    analytics.searchAppClick(view.getViewModel()
            .getSearchQueryModel(), packageName, searchApp.getPosition(), searchApp.getSearchAppResult()
            .isAppcApp(), isMigration, !searchApp.getSearchAppResult().getSplits().isEmpty(),
        searchApp.getSearchAppResult().getObb() != null,
        searchApp.getSearchAppResult().getVersionCode(),
        searchApp.getSearchAppResult().isInCatappult(),
        searchApp.getSearchAppResult().getAppCategory());
    navigator.goToAppView(appId, packageName, view.getViewModel()
        .getStoreTheme(), storeName);
  }

  private Completable loadData(String query, String storeName, List<Filter> filters,
      boolean useCachedValues) {
    if (storeName != null && !storeName.trim()
        .equals("")) {
      return searchManager.searchInStore(query, storeName, filters, useCachedValues);
    }
    // search every store. followed and not followed
    return searchManager.searchAppInStores(query, filters, useCachedValues);
  }

  @VisibleForTesting public void doFirstSearch() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .flatMapCompletable(model -> search(model, model.hasLoadedResults()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  public void redoSearchAfterAdultContentSwitch() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.adultContentDialogPositiveClick(),
            view.adultContentWithPinDialogPositiveClick()))
        .map(__ -> view.getViewModel())
        .flatMapCompletable(model -> search(model, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  public void updateAdultContentSwitchOnNoResults() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .flatMap(__ -> view.viewHasNoResults())
        .flatMap(__ -> searchManager.isAdultContentEnabled())
        .doOnNext(adultContent -> view.setAdultContentSwitch(adultContent))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  public Completable search(SearchResultView.Model resultModel, boolean useCachedValues) {
    return Observable.just(resultModel)
        .filter(viewModel -> hasValidQuery(viewModel))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideSuggestionsViews())
        .doOnNext(__ -> view.showLoading())
        .observeOn(ioScheduler)
        .first()
        .toSingle()
        .flatMapCompletable(viewModel -> loadData(viewModel.getSearchQueryModel()
            .getFinalQuery(), viewModel.getStoreName(), viewModel.getFilters(), useCachedValues));
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
          SearchQueryModel searchQueryModel = new SearchQueryModel(data.getQuery());
          analytics.search(searchQueryModel);
          navigator.navigate(searchQueryModel);
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
          SearchQueryModel searchQueryModel =
              new SearchQueryModel(data.first, data.second.getQuery(),
                  data.first.isEmpty() ? Source.FROM_TRENDING : Source.FROM_AUTOCOMPLETE);
          navigator.navigate(searchQueryModel);
          analytics.searchFromSuggestion(searchQueryModel, data.second.getPosition());
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
