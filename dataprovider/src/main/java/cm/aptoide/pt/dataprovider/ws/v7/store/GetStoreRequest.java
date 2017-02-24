/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */

@EqualsAndHashCode(callSuper = true) public class GetStoreRequest
    extends BaseRequestWithStore<GetStore, GetHomeAndStoreBody> {

  private final String url;

  private GetStoreRequest(String url, String baseHost, GetHomeAndStoreBody body) {
    super(body, baseHost);
    this.url = url;
  }

  public static GetStoreRequest of(StoreCredentials storeCredentials, StoreContext storeContext,
      String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    final GetHomeAndStoreBody body =
        new GetHomeAndStoreBody(storeCredentials, WidgetsArgs.createDefault());
    body.setContext(storeContext);

    return new GetStoreRequest("", BASE_HOST,
        (GetHomeAndStoreBody) decorator.decorate(body, accessToken));
  }

  public static GetStoreRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    final GetHomeAndStoreBody body =
        new GetHomeAndStoreBody(storeCredentials, WidgetsArgs.createDefault());

    return new GetStoreRequest(new V7Url(url).remove("getStore").get(), BASE_HOST,
        (GetHomeAndStoreBody) decorator.decorate(body, accessToken));
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getStore(url, body, bypassCache);
  }
}
