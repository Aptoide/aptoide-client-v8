/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreWidgetsRequest
    extends BaseRequestWithStore<GetStoreWidgets, GetStoreWidgetsRequest.Body> {

  private final String url;

  private GetStoreWidgetsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, httpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
    this.url = url;
  }

  public static GetStoreWidgetsRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager) {

    final Body body =
        new Body(storeCredentials, WidgetsArgs.createDefault(resources, windowManager));

    return new GetStoreWidgetsRequest(new V7Url(url).remove("getStoreWidgets")
        .get(), body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);
  }

  public String getUrl() {
    return url;
  }

  @Override protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreWidgets(url, body, bypassCache);
  }

  public static class Body extends BaseBodyWithStore {

    private WidgetsArgs widgetsArgs;
    private StoreContext context;
    private String storeName;

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

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }

    public StoreContext getContext() {
      return context;
    }

    @Override public String getStoreName() {
      return storeName;
    }
  }
}
