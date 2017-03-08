/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetHomeMetaRequest
    extends BaseRequestWithStore<GetHomeMeta, GetHomeMetaRequest.Body> {

  private String url;

  private GetHomeMetaRequest(String baseHost, Body body) {
    super(body, baseHost);
  }

  private GetHomeMetaRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  public static GetHomeMetaRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor interceptor) {
    return new GetHomeMetaRequest(new V7Url(url).remove("home/getMeta").get(),
        (Body) interceptor.intercept(new Body(storeCredentials)), BASE_HOST);
  }

  public static GetHomeMetaRequest of(StoreCredentials storeCredentials,
      BodyInterceptor interceptor) {
    return new GetHomeMetaRequest(BASE_HOST,
        (Body) interceptor.intercept(new Body(storeCredentials)));
  }

  @Override protected Observable<GetHomeMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getHomeMeta(url != null ? url : "", body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
