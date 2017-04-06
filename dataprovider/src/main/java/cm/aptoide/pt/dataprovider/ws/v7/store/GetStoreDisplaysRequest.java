/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreDisplaysRequest
    extends BaseRequestWithStore<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

  private String url;

  GetStoreDisplaysRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static GetStoreDisplaysRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {

    return new GetStoreDisplaysRequest(new V7Url(url).remove("getStoreDisplays").get(),
        new Body(storeCredentials), bodyInterceptor);
  }

  @Override protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreDisplays(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
