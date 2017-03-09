package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class GetStoreMetaRequest
    extends BaseRequestWithStore<GetStoreMeta, GetHomeMetaRequest.Body> {

  public GetStoreMetaRequest(GetHomeMetaRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials,
      BodyInterceptor bodyInterceptor) {

    return new GetStoreMetaRequest((GetHomeMetaRequest.Body) bodyInterceptor.intercept(
        new GetHomeMetaRequest.Body(storeCredentials)), BASE_HOST);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreMeta(body, bypassCache);
  }
}
