package cm.aptoide.pt.search;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.download.view.DownloadStatusManager;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchFilterType;
import cm.aptoide.pt.search.model.SearchFilters;
import cm.aptoide.pt.search.model.SearchResult;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppScreenshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

@SuppressWarnings("Convert2MethodRef") public class SearchManager {

  private final AptoideAccountManager accountManager;
  private final SearchRepository searchRepository;
  private final DownloadStatusManager downloadStatusManager;
  private final AppCenter appCenter;

  public SearchManager(AptoideAccountManager accountManager, SearchRepository searchRepository,
      DownloadStatusManager downloadStatusManager, AppCenter appCenter) {
    this.accountManager = accountManager;
    this.searchRepository = searchRepository;
    this.downloadStatusManager = downloadStatusManager;
    this.appCenter = appCenter;
  }

  public Completable searchAppInStores(String query, List<Filter> filters,
      boolean useCachedValues) {
    return accountManager.hasMatureContentEnabled()
        .first()
        .toSingle()
        .flatMapCompletable(
            matureEnabled -> searchRepository.generalSearch(query, getSearchFilters(filters),
                matureEnabled, useCachedValues));
  }

  public Observable<SearchResult> observeSearchResults() {
    return searchRepository.observeSearchResults()
        .switchMap(result -> {
          List<SearchAppResult> list = result.getSearchResultsList();
          if (!list.isEmpty() && list.get(0)
              .isHighlightedResult()) {
            if (result.isFreshResult()) {
              return Observable.mergeDelayError(Observable.just(result),
                  observeHighlightedSearchResult(result));
            }
            return observeHighlightedSearchResult(result);
          }
          return Observable.just(result);
        });
  }

  public Observable<SearchResult> observeHighlightedSearchResult(SearchResult result) {
    SearchAppResult first = result.getSearchResultsList()
        .get(0);
    return appCenter.unsafeLoadDetailedApp(first.getAppId(), first.getStoreName(),
            first.getPackageName()).toObservable()
        .flatMap(app ->
            Observable.combineLatest(getHighlightedSearchResult(result),
                downloadStatusManager.loadDownloadModel(first.getMd5(), first.getPackageName(),
                    first.getVersionCode(), app.getDetailedApp().getSignature(), first.getStoreId(),
                    first.hasAdvertising() || first.hasBilling()),
                loadAppScreenShots(first.getAppId(), first.getStoreName(), first.getPackageName()),
                (r, downloadModel, screenshots) -> mergeSearchResult(r, downloadModel,
                    screenshots, app.getDetailedApp().getBdsFlags().contains("STORE_BDS"),
                    app.getDetailedApp().getAppCategory())));
  }

  private Observable<List<AppScreenshot>> loadAppScreenShots(long appId, String storeName,
      String packageName) {
    return Observable.mergeDelayError(Observable.just(null),
            appCenter.unsafeLoadDetailedApp(appId, storeName, packageName)
                .toObservable())
        .map(app -> {
          List<AppScreenshot> ssList = Collections.emptyList();
          if (app != null
              && app.getDetailedApp() != null
              && app.getDetailedApp()
              .getMedia() != null
              && app.getDetailedApp()
              .getMedia()
              .getScreenshots() != null) {
            ssList = app.getDetailedApp()
                .getMedia()
                .getScreenshots();
          }
          return ssList;
        })
        .throttleLast(700, TimeUnit.MILLISECONDS);
  }

  private SearchResult mergeSearchResult(SearchResult r, DownloadStatusModel downloadStatusModel,
      List<AppScreenshot> screenshots, boolean isInCatappult, String appCategory) {
    ArrayList<SearchAppResult> list = new ArrayList<>(r.getSearchResultsList());
    list.set(0, new SearchAppResult(list.get(0), downloadStatusModel, screenshots, isInCatappult,
        appCategory));
    return new SearchResult(r.getQuery(), r.getSpecificStore(), list, r.getFilters(),
        r.getCurrentOffset(), r.getNextOffset(), r.getTotal(), r.getLoading(), r.isFreshResult(),
        r.getError());
  }

  private Observable<SearchResult> getHighlightedSearchResult(SearchResult r) {
    return Observable.just(
        new SearchResult(r.getQuery(), r.getSpecificStore(), r.getSearchResultsList(),
            r.getFilters(), r.getCurrentOffset(), r.getNextOffset(), r.getTotal(), r.getLoading(),
            false, r.getError()));
  }

  public SearchFilters getSearchFilters(List<Filter> viewFilters) {
    boolean onlyFollowedStores = false;
    boolean onlyTrustedApps = false;
    boolean onlyBetaApps = false;
    boolean onlyAppcApps = false;
    for (Filter filter : viewFilters) {
      if (filter.getIdentifier() == null) continue;
      if (filter.getIdentifier()
          .equals(SearchFilterType.FOLLOWED_STORES.name())) {
        onlyFollowedStores = filter.getSelected();
      } else if (filter.getIdentifier()
          .equals(SearchFilterType.TRUSTED.name())) {
        onlyTrustedApps = filter.getSelected();
      } else if (filter.getIdentifier()
          .equals(SearchFilterType.BETA.name())) {
        onlyBetaApps = filter.getSelected();
      } else if (filter.getIdentifier()
          .equals(SearchFilterType.APPC.name())) {
        onlyAppcApps = filter.getSelected();
      }
    }
    return new SearchFilters(onlyFollowedStores, onlyTrustedApps, onlyBetaApps, onlyAppcApps);
  }

  public Completable searchInStore(String query, String storeName, List<Filter> filters,
      boolean useCachedValues) {
    return accountManager.hasMatureContentEnabled()
        .first()
        .toSingle()
        .flatMapCompletable(
            matureEnabled -> searchRepository.searchInStore(query, getSearchFilters(filters),
                matureEnabled, storeName, useCachedValues));
  }

  public Completable disableAdultContent() {
    return accountManager.disable();
  }

  public Completable enableAdultContent() {
    return accountManager.enable();
  }

  public Observable<Boolean> isAdultContentEnabled() {
    return accountManager.hasMatureContentEnabled();
  }

  public Observable<Boolean> isAdultContentPinRequired() {
    return accountManager.pinRequired();
  }

  public Completable enableAdultContentWithPin(int pin) {
    return accountManager.enable(pin);
  }
}
