/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListStoresRequest extends V7<ListStores, ListStoresRequest.Body> {

  static final String STORT_BY_DOWNLOADS = "downloads7d";
  private String url;

  private ListStoresRequest(String url, Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
    this.url = url;
  }

  private ListStoresRequest(Body body, String baseHost, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
  }

  public static ListStoresRequest ofTopStores(int offset, int limit,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    final Body baseBody = new Body();
    baseBody.setOffset(offset);
    baseBody.limit = limit;
    return new ListStoresRequest(baseBody, BASE_HOST, bodyInterceptor, httpClient,
        converterFactory);
  }

  public static ListStoresRequest ofAction(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {

    url = url.replace("listStores", "");
    if (!url.startsWith("/")) {
      url = "/" + url;
    }
    return new ListStoresRequest(url, new Body(), BASE_HOST, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override
  protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    if (TextUtils.isEmpty(url)) {
      return interfaces.listTopStores(STORT_BY_DOWNLOADS, 10, body, bypassCache);
    } else {
      return interfaces.listStores(url, body, bypassCache);
    }
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;

    public Body() {
    }
  }
}
