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
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreWidgetsRequest
    extends BaseRequestWithStore<GetStoreWidgets, GetStoreWidgetsRequest.Body> {

  private final String url;

  private GetStoreWidgetsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, httpClient, converterFactory, bodyInterceptor);
    this.url = url;
  }

  public static GetStoreWidgetsRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    final Body body = new Body(storeCredentials, WidgetsArgs.createDefault());

    return new GetStoreWidgetsRequest(new V7Url(url).remove("getStoreWidgets")
        .get(), body, bodyInterceptor, httpClient, converterFactory);
  }

  @Override protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreWidgets(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    @Getter private WidgetsArgs widgetsArgs;
    @Getter private StoreContext context;
    @Getter private String storeName;

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
    }

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs,
        StoreContext storeContext, String storeName) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
      this.context = storeContext;
      this.storeName = storeName;
    }
  }
}
