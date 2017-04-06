package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class GetStoreMetaRequest
    extends BaseRequestWithStore<GetStoreMeta, GetHomeMetaRequest.Body> {

  public GetStoreMetaRequest(GetHomeMetaRequest.Body body,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    return new GetStoreMetaRequest(new GetHomeMetaRequest.Body(storeCredentials), bodyInterceptor);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreMeta(body, bypassCache);
  }
}
