package cm.aptoide.pt.v8engine.repository.request;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 03-01-2017.
 */
class ListAppsRequestFactory {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final WindowManager windowManager;

  public ListAppsRequestFactory(BodyInterceptor<BaseBody> bodyInterceptor,
      StoreCredentialsProvider storeCredentialsProvider, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.windowManager = windowManager;
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, resources,
        windowManager);
  }

  public ListAppsRequest newListAppsRequest(int storeId, Long groupId, int limit,
      ListAppsRequest.Sort sort) {
    return new ListAppsRequest(
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), groupId, limit,
            sharedPreferences, sort), bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }
}
