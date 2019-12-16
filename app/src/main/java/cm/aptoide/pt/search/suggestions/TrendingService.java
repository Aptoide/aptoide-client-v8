package cm.aptoide.pt.search.suggestions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by franciscocalado on 11/14/17.
 */

public class TrendingService {
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final AppBundlesVisibilityManager appBundlesVisibilityManager;

  public TrendingService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
  }

  public Observable<ListApps> getTrendingApps(int limit, int storeId) {
    ListAppsRequest.Body body =
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), limit, sharedPreferences,
            ListAppsRequest.Sort.trending30d);
    body.setStoreId(storeId);
    return new ListAppsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, appBundlesVisibilityManager).observe(false);
  }
}
