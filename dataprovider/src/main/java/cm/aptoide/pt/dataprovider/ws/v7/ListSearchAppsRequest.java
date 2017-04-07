/*
 * Copyright (c) 2016.
 * Modified on 05/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

  private ListSearchAppsRequest(Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
  }

  public static ListSearchAppsRequest of(String query, String storeName,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    List<String> stores = null;
    if (storeName != null) {
      stores = Collections.singletonList(storeName);
    }

    if (subscribedStoresAuthMap != null && subscribedStoresAuthMap.containsKey(storeName)) {
      HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
      storesAuthMap.put(storeName, subscribedStoresAuthMap.get(storeName));
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, storesAuthMap, stores, false), BASE_HOST,
          bodyInterceptor, httpClient, converterFactory);
    }
    return new ListSearchAppsRequest(new Body(Endless.DEFAULT_LIMIT, query, stores, false),
        BASE_HOST, bodyInterceptor, httpClient, converterFactory);
  }

  public static ListSearchAppsRequest of(String query, boolean addSubscribedStores,
      List<Long> subscribedStoresIds, HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    if (addSubscribedStores) {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, subscribedStoresIds, subscribedStoresAuthMap,
              false), BASE_HOST, bodyInterceptor, httpClient, converterFactory);
    } else {
      return new ListSearchAppsRequest(new Body(Endless.DEFAULT_LIMIT, query, false), BASE_HOST,
          bodyInterceptor, httpClient, converterFactory);
    }
  }

  public static ListSearchAppsRequest of(String query, boolean addSubscribedStores,
      boolean trustedOnly, List<Long> subscribedStoresIds,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    if (addSubscribedStores) {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, subscribedStoresIds, null, trustedOnly), BASE_HOST,
          bodyInterceptor, httpClient, converterFactory);
    } else {
      return new ListSearchAppsRequest(new Body(Endless.DEFAULT_LIMIT, query, trustedOnly),
          BASE_HOST, bodyInterceptor, httpClient, converterFactory);
    }
  }

  @Override protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listSearchApps(body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithAlphaBetaKey
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    @Getter private String query;
    @Getter private List<Long> storeIds;
    @Getter private List<String> storeNames;
    @Getter private HashMapNotNull<String, List<String>> storesAuthMap;
    @Getter private Boolean trusted;

    public Body(Integer limit, String query, List<Long> storeIds,
        HashMapNotNull<String, List<String>> storesAuthMap, Boolean trusted) {
      this.limit = limit;
      this.query = query;
      this.storeIds = storeIds;
      this.storesAuthMap = storesAuthMap;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, List<String> storeNames, Boolean trusted) {
      this.limit = limit;
      this.query = query;
      this.storeNames = storeNames;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, HashMapNotNull<String, List<String>> storesAuthMap,
        List<String> storeNames, Boolean trusted) {
      this.limit = limit;
      this.query = query;
      this.storesAuthMap = storesAuthMap;
      this.storeNames = storeNames;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, Boolean trusted) {
      this.limit = limit;
      this.query = query;
      this.trusted = trusted;
    }
  }
}
