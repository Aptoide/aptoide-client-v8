package cm.aptoide.pt.search;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchFilterType;
import cm.aptoide.pt.search.model.SearchFilters;
import cm.aptoide.pt.search.model.SearchResult;
import cm.aptoide.pt.search.model.SearchResultError;
import java.net.UnknownHostException;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

@SuppressWarnings("Convert2MethodRef") public class SearchManager {

  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final HashMapNotNull<String, List<String>> subscribedStoresAuthMap;
  private final AdsRepository adsRepository;
  private final AptoideAccountManager accountManager;
  private final MoPubAdsManager moPubAdsManager;
  private final AppBundlesVisibilityManager appBundlesVisibilityManager;
  private final SearchRepository searchRepository;

  public SearchManager(SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap, AdsRepository adsRepository,
      AptoideAccountManager accountManager, MoPubAdsManager moPubAdsManager,
      AppBundlesVisibilityManager appBundlesVisibilityManager, SearchRepository searchRepository) {
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.subscribedStoresAuthMap = subscribedStoresAuthMap;
    this.adsRepository = adsRepository;
    this.accountManager = accountManager;
    this.moPubAdsManager = moPubAdsManager;
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
    this.searchRepository = searchRepository;
  }

  public Observable<SearchAdResult> getAdsForQuery(String query) {
    return adsRepository.getAdsFromSearch(query)
        .map(minimalAd -> new SearchAdResult(minimalAd));
  }

  public Single<SearchResult> searchAppInStores(String query, List<Filter> filters) {
    return accountManager.hasMatureContentEnabled()
        .first()
        .toSingle()
        .flatMap(matureEnabled -> searchRepository.generalSearch(query, getSearchFilters(filters),
            matureEnabled));
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

  public Single<SearchResult> searchInStore(String query, String storeName, int offset) {
    return ListSearchAppsRequest.of(query, storeName, false, false, offset, subscribedStoresAuthMap,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
        appBundlesVisibilityManager)
        .observe(false)
        .flatMap(results -> handleSearchResults(query, results))
        .onErrorResumeNext(throwable -> handleSearchError(query, throwable))
        .doOnError(throwable -> throwable.printStackTrace())
        .toSingle();
  }

  private Observable<SearchResult> handleSearchResults(String query, ListSearchApps results) {
    return Observable.just(results)
        .filter(listSearchApps -> hasResults(listSearchApps))
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(searchApp -> new SearchAppResult(searchApp))
        .toList()
        .first()
        .map(list -> new SearchResult(query, new SearchResultDiffModel(null, list)));
  }

  private Observable<SearchResult> handleSearchError(String query, Throwable throwable) {
    if (throwable instanceof UnknownHostException
        || throwable instanceof NoNetworkConnectionException) {
      return Observable.just(new SearchResult(query, SearchResultError.NO_NETWORK));
    }
    return Observable.just(new SearchResult(query, SearchResultError.GENERIC));
  }

  private boolean hasResults(ListSearchApps listSearchApps) {
    DataList<SearchApp> dataList = listSearchApps.getDataList();
    return dataList != null
        && dataList.getList() != null
        && dataList.getList()
        .size() > 0;
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return moPubAdsManager.shouldLoadBannerAd();
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return moPubAdsManager.shouldLoadNativeAds();
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
