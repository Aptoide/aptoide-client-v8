package cm.aptoide.pt.search;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
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

  public SearchManager(SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient, Converter.Factory converterFactory,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap, List<Long> subscribedStoresIds) {
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.subscribedStoresAuthMap = subscribedStoresAuthMap;
    this.subscribedStoresIds = subscribedStoresIds;
  }

  public Observable<ListSearchApps> searchInNonFollowedStores(String query,
      boolean onlyTrustedApps) {
    return ListSearchAppsRequest.of(query, false, onlyTrustedApps, subscribedStoresIds,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  public Observable<ListSearchApps> searchInFollowedStores(String query,
      boolean onlyTrustedApps) {
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
