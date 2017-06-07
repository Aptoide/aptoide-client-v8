package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class GetStoreMetaRequest
    extends BaseRequestWithStore<GetStoreMeta, GetHomeMetaRequest.Body> {

  public GetStoreMetaRequest(GetHomeMetaRequest.Body body,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(body, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return new GetStoreMetaRequest(new GetHomeMetaRequest.Body(storeCredentials), bodyInterceptor,
        httpClient, converterFactory);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreMeta(body, bypassCache);
  }
}
