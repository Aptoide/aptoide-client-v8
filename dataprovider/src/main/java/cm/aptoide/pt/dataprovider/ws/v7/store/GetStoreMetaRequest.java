package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
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

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetStoreMetaRequest(
        (GetHomeMetaRequest.Body) decorator.decorate(new GetHomeMetaRequest.Body(storeCredentials),
            accessToken), BASE_HOST);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreMeta(body, bypassCache);
  }
}
