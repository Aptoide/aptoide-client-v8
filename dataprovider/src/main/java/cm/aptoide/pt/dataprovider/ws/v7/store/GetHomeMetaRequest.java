/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
public class GetHomeMetaRequest extends BaseRequestWithStore<GetHomeMeta, GetHomeMetaRequest.Body> {

  private final String url;

  private GetHomeMetaRequest(Body body, String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, httpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
    this.url = url;
  }

  public static GetHomeMetaRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new GetHomeMetaRequest(new Body(storeCredentials), new V7Url(url).remove("home/getMeta")
        .get(), bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  public String getUrl() {
    return url;
  }

  @Override protected Observable<GetHomeMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getHomeMeta(url != null ? url : "", body, bypassCache);
  }

  public static class Body extends BaseBodyWithStore {

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
