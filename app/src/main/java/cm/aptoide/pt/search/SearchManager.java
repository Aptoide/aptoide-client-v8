package cm.aptoide.pt.search;

import android.content.SharedPreferences;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.logger.Logger;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class SearchManager {

  private static final String TAG = SearchManager.class.getName();

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

  public Observable<MinimalAd> getAdsForQuery(String query) {
    return adsRepository.getAdsFromSearch(query)
        .onErrorReturn(throwable -> {
          Logger.e(TAG, throwable);
          return null;
        });
  }

  public Observable<ListSearchApps> searchInNonFollowedStores(String query,
      boolean onlyTrustedApps) {
    return ListSearchAppsRequest.of(query, false, onlyTrustedApps, subscribedStoresIds,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  public Observable<ListSearchApps> searchInFollowedStores(String query, boolean onlyTrustedApps) {
    return ListSearchAppsRequest.of(query, true, onlyTrustedApps, subscribedStoresIds,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  public Observable<ListSearchApps> searchInStore(String query, String storeName) {
    return ListSearchAppsRequest.of(query, storeName, subscribedStoresAuthMap, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }
}
