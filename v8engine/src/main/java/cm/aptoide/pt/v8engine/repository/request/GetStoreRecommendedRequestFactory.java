package cm.aptoide.pt.v8engine.repository.request;

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

  public GetStoreRecommendedRequestFactory(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
  }

  public GetRecommendedStoresRequest newRecommendedStore(String url) {
    return GetRecommendedStoresRequest.ofAction(url, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator);
  }
}
