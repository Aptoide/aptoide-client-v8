/*
 * Copyright (c) 2016.
 * Modified on 05/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

  private ListSearchAppsRequest(Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static ListSearchAppsRequest of(String query, String storeName,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    List<String> stores = null;
    if (storeName != null) {
      stores = Collections.singletonList(storeName);
    }

    if (subscribedStoresAuthMap != null && subscribedStoresAuthMap.containsKey(storeName)) {
      HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
      storesAuthMap.put(storeName, subscribedStoresAuthMap.get(storeName));
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, storesAuthMap, stores, false, sharedPreferences),
          getHost(sharedPreferences), bodyInterceptor, httpClient, converterFactory,
          tokenInvalidator);
    }
    return new ListSearchAppsRequest(
        new Body(Endless.DEFAULT_LIMIT, query, stores, false, sharedPreferences),
        getHost(sharedPreferences), bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator);
  }

  public static ListSearchAppsRequest of(String query, boolean addSubscribedStores,
      List<Long> subscribedStoresIds, HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    if (addSubscribedStores) {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, subscribedStoresIds, subscribedStoresAuthMap,
              false, sharedPreferences), getHost(sharedPreferences), bodyInterceptor, httpClient,
          converterFactory, tokenInvalidator);
    } else {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, false, sharedPreferences),
          getHost(sharedPreferences), bodyInterceptor, httpClient, converterFactory,
          tokenInvalidator);
    }
  }

  public static ListSearchAppsRequest of(String query, boolean addSubscribedStores,
      boolean trustedOnly, List<Long> subscribedStoresIds,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    if (addSubscribedStores) {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, subscribedStoresIds, null, trustedOnly,
              sharedPreferences), getHost(sharedPreferences), bodyInterceptor, httpClient,
          converterFactory, tokenInvalidator);
    } else {
      return new ListSearchAppsRequest(
          new Body(Endless.DEFAULT_LIMIT, query, trustedOnly, sharedPreferences),
          getHost(sharedPreferences), bodyInterceptor, httpClient, converterFactory,
          tokenInvalidator);
    }
  }

  @Override protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listSearchApps(body, bypassCache);
  }

  public static class Body extends BaseBodyWithAlphaBetaKey implements Endless {

    private int offset;
    private Integer limit;
    private String query;
    private List<Long> storeIds;
    private List<String> storeNames;
    private HashMapNotNull<String, List<String>> storesAuthMap;
    private Boolean trusted;

    public Body(Integer limit, String query, List<Long> storeIds,
        HashMapNotNull<String, List<String>> storesAuthMap, Boolean trusted,
        SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.limit = limit;
      this.query = query;
      this.storeIds = storeIds;
      this.storesAuthMap = storesAuthMap;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, List<String> storeNames, Boolean trusted,
        SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.limit = limit;
      this.query = query;
      this.storeNames = storeNames;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, HashMapNotNull<String, List<String>> storesAuthMap,
        List<String> storeNames, Boolean trusted, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.limit = limit;
      this.query = query;
      this.storesAuthMap = storesAuthMap;
      this.storeNames = storeNames;
      this.trusted = trusted;
    }

    public Body(Integer limit, String query, Boolean trusted, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.limit = limit;
      this.query = query;
      this.trusted = trusted;
    }

    public String getQuery() {
      return query;
    }

    public List<Long> getStoreIds() {
      return storeIds;
    }

    public List<String> getStoreNames() {
      return storeNames;
    }

    public HashMapNotNull<String, List<String>> getStoresAuthMap() {
      return storesAuthMap;
    }

    public Boolean getTrusted() {
      return trusted;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }
}
