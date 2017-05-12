package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequestFactory {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public GetUserRequestFactory(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public GetUserRequest newGetUser(String url) {
    return GetUserRequest.of(url, bodyInterceptor, httpClient, converterFactory);
  }
}
