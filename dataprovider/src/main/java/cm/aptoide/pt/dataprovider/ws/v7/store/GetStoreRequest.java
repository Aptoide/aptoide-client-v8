/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */

@EqualsAndHashCode(callSuper = true) public class GetStoreRequest
    extends BaseRequestWithStore<GetStore, GetStoreRequest.Body> {

  private final String url;

  private GetStoreRequest(String url, String baseHost, Body body) {
    super(body, baseHost);
    this.url = url;
  }

  public static GetStoreRequest of(StoreCredentials storeCredentials, StoreContext storeContext,
      String accessToken, BodyInterceptor bodyInterceptor) {

    final Body body = new Body(storeCredentials, WidgetsArgs.createDefault());
    body.setContext(storeContext);

    return new GetStoreRequest("", BASE_HOST, (Body) bodyInterceptor.intercept(body));
  }

  public static GetStoreRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken, BodyInterceptor bodyInterceptor) {

    final Body body = new Body(storeCredentials, WidgetsArgs.createDefault());

    return new GetStoreRequest(new V7Url(url).remove("getStore").get(), BASE_HOST,
        (Body) bodyInterceptor.intercept(body));
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getStore(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    @Getter private final WidgetsArgs widgetsArgs;
    @Getter @Setter private StoreContext context;

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
    }
  }
}
