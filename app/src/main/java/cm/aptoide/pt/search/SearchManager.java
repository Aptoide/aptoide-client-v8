package cm.aptoide.pt.search;

import android.content.SharedPreferences;
import cm.aptoide.pt.ads.AdsRepository;
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
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class SearchManager {

  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final HashMapNotNull<String, List<String>> subscribedStoresAuthMap;
  private final List<Long> subscribedStoresIds;
  private final AdsRepository adsRepository;

  public SearchManager(SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap, List<Long> subscribedStoresIds,
      AdsRepository adsRepository) {
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.subscribedStoresAuthMap = subscribedStoresAuthMap;
    this.subscribedStoresIds = subscribedStoresIds;
    this.adsRepository = adsRepository;
  }

  public Observable<SearchAdResult> getAdsForQuery(String query) {
    return adsRepository.getAdsFromSearch(query)
        .map(SearchAdResult::new);
  }

  public Observable<List<SearchAppResult>> searchInNonFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return ListSearchAppsRequest.of(query, offset, false, onlyTrustedApps, subscribedStoresIds,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .filter(this::hasResults)
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(SearchAppResult::new)
        .toList();
  }

  public Observable<List<SearchAppResult>> searchInFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return ListSearchAppsRequest.of(query, offset, true, onlyTrustedApps, subscribedStoresIds,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .filter(this::hasResults)
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(SearchAppResult::new)
        .toList();
  }

  public Observable<List<SearchAppResult>> searchInStore(String query, String storeName,
      int offset) {
    return ListSearchAppsRequest.of(query, storeName, offset, subscribedStoresAuthMap,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .filter(this::hasResults)
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(SearchAppResult::new)
        .toList();
  }

  public Observable<List<String>> getTrendingApps(){
    List<String> test = new ArrayList<>();
    test.add("Facebook");
    test.add("Twitter");
    test.add("Google");
    test.add("Hill Climb Racing");
    test.add("Aptoide");
    return Observable.just(test);

  }

  private boolean hasResults(ListSearchApps listSearchApps) {
    DataList<SearchApp> dataList = listSearchApps.getDataList();
    return dataList != null
        && dataList.getList() != null
        && dataList.getList()
        .size() > 0;
  }
}
