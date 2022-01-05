package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetRecommendedStoresRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 21/03/2017.
 */

public class GetStoreRecommendedRequestFactory {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public GetStoreRecommendedRequestFactory(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public GetRecommendedStoresRequest newRecommendedStore(String url) {
    return GetRecommendedStoresRequest.ofAction(url, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }
}
