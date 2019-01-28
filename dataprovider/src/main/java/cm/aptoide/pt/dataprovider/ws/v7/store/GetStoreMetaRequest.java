package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class GetStoreMetaRequest
    extends BaseRequestWithStore<GetStoreMeta, GetHomeMetaRequest.Body> {

  private final String url;

  public GetStoreMetaRequest(String url, GetHomeMetaRequest.Body body,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, httpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
    this.url = url;
  }

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new GetStoreMetaRequest("", new GetHomeMetaRequest.Body(storeCredentials),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  public static GetStoreMetaRequest ofAction(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetStoreMetaRequest(url, new GetHomeMetaRequest.Body(new StoreCredentials()),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (!url.isEmpty()) {
      return interfaces.getStoreMeta(url, body, bypassCache);
    } else {
      return interfaces.getStoreMeta(body, bypassCache);
    }
  }
}
