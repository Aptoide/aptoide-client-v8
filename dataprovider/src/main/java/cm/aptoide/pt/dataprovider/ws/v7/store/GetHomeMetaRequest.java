/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetHomeMetaRequest
    extends BaseRequestWithStore<GetHomeMeta, GetHomeMetaRequest.Body> {

  private final String url;

  private GetHomeMetaRequest(Body body, String url, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static GetHomeMetaRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    return new GetHomeMetaRequest(new Body(storeCredentials),
        new V7Url(url).remove("home/getMeta").get(), bodyInterceptor);
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
