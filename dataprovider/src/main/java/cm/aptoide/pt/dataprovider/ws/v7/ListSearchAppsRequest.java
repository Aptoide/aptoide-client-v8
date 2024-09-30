/*
 * Copyright (c) 2016.
 * Modified on 05/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

  private static final int LIMIT = 15;

  private ListSearchAppsRequest(Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_CACHE_HOST
        + "/api/7.20240701/";
  }

  public static ListSearchAppsRequest of(String query, int offset, String storeName,
      boolean trustedOnly, boolean betaOnly, boolean appcOnly, Boolean isMature,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    List<String> stores = null;
    if (storeName != null) {
      stores = Collections.singletonList(storeName);
    }
    final Body body =
        new Body(LIMIT, offset, query, subscribedStoresAuthMap, stores, trustedOnly, betaOnly,
            appcOnly, sharedPreferences, isMature);

    return new ListSearchAppsRequest(body, getHost(sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  public static ListSearchAppsRequest of(String query, int offset, boolean onlyFollowedStores,
      boolean trustedOnly, boolean betaOnly, boolean appcOnly, Boolean isMature,
      List<Long> subscribedStoresIds, HashMapNotNull<String, List<String>> subscribedStoresAuthMap,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    final Body body;
    if (onlyFollowedStores) {
      body =
          new Body(LIMIT, offset, query, subscribedStoresIds, subscribedStoresAuthMap, trustedOnly,
              betaOnly, appcOnly, sharedPreferences, isMature);
    } else {
      body = new Body(LIMIT, offset, query, trustedOnly, betaOnly, appcOnly, sharedPreferences,
          isMature);
    }
    return new ListSearchAppsRequest(body, getHost(sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  @Override protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listSearchApps(bypassCache ? "no-cache" : null,
        getQueryStringMapper().map(body));
  }

  public static class Body extends BaseBodyWithAlphaBetaKey implements Endless {

    private int offset;
    private Integer limit;
    private String query;
    private List<Long> storeIds;
    private List<String> storeNames;
    private HashMapNotNull<String, List<String>> storesAuthMap;

    private Boolean onlyBeta;
    private Boolean onlyTrusted;
    private Boolean onlyAppc;

    public Body(Integer limit, int offset, String query, List<Long> storeIds,
        HashMapNotNull<String, List<String>> storesAuthMap, Boolean trusted, Boolean onlyBeta,
        Boolean onlyAppc, SharedPreferences sharedPreferences, Boolean isMature) {
      super(sharedPreferences);
      this.limit = limit;
      this.offset = offset;
      this.query = query;
      this.storeIds = storeIds;
      this.storesAuthMap = storesAuthMap;
      this.onlyTrusted = trusted;
      this.setMature(isMature);
      this.onlyBeta = onlyBeta;
      this.onlyAppc = onlyAppc;
    }

    public Body(Integer limit, int offset, String query,
        HashMapNotNull<String, List<String>> storesAuthMap, List<String> storeNames,
        Boolean onlyTrusted, Boolean onlyBeta, Boolean onlyAppc,
        SharedPreferences sharedPreferences, Boolean isMature) {
      super(sharedPreferences);
      this.limit = limit;
      this.offset = offset;
      this.query = query;
      this.storesAuthMap = storesAuthMap;
      this.storeNames = storeNames;
      this.onlyTrusted = onlyTrusted;
      this.onlyBeta = onlyBeta;
      this.onlyAppc = onlyAppc;
      this.setMature(isMature);
    }

    public Body(Integer limit, int offset, String query, Boolean trusted, Boolean onlyBeta,
        Boolean onlyAppc, SharedPreferences sharedPreferences, Boolean isMature) {
      super(sharedPreferences);
      this.limit = limit;
      this.offset = offset;
      this.query = query;
      this.onlyTrusted = trusted;
      this.setMature(isMature);
      this.onlyBeta = onlyBeta;
      this.onlyAppc = onlyAppc;
    }

    public String getQuery() {
      return query;
    }

    public List<Long> getStoreIds() {
      return storeIds;
    }

    public String getStoreIdsAsString() {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < storeIds.size(); i++) {
        if (i != storeIds.size() - 1) {
          stringBuilder.append(storeIds.get(i))
              .append(",");
        } else {
          stringBuilder.append(storeIds.get(i));
        }
      }
      return stringBuilder.toString();
    }

    public List<String> getStoreNames() {
      return storeNames;
    }

    public String getStoreNamesAsString() {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < storeNames.size(); i++) {
        if (i != storeNames.size() - 1) {
          stringBuilder.append(storeNames.get(i))
              .append(",");
        } else {
          stringBuilder.append(storeNames.get(i));
        }
      }
      return stringBuilder.toString();
    }

    public HashMapNotNull<String, List<String>> getStoresAuthMap() {
      return storesAuthMap;
    }

    public Boolean getOnlyBeta() {
      return onlyBeta;
    }

    public Boolean getOnlyTrusted() {
      return onlyTrusted;
    }

    public Boolean getOnlyAppc() {
      return onlyAppc;
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

    public String getStoresAuthMapAsString() {
      ObjectMapper objectMapper = new ObjectMapper();

      String json = null;
      try {
        json = objectMapper.writeValueAsString(storesAuthMap);
        System.out.println("json = " + json);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return json;
    }

    @Override public boolean shouldIncludeTag() {
      return !onlyBeta;
    }
  }
}
