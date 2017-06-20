/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 04-07-2016.
 */

/**
 * http://ws2.aptoide.com/api/7/listFullReviews/info/1
 * <p>
 * http://ws2.aptoide.com/api/7/listReviews/info/1
 */
public class ListReviewsRequest extends V7<ListReviews, ListReviewsRequest.Body> {

  private static final int MAX_REVIEWS = 10;
  private static final int MAX_COMMENTS = 10;

  private ListReviewsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static ListReviewsRequest of(String storeName, String packageName,
      BaseRequestWithStore.StoreCredentials storecredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return of(storeName, packageName, MAX_REVIEWS, MAX_COMMENTS, storecredentials, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  /**
   * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
   */
  public static ListReviewsRequest of(String storeName, String packageName, int maxReviews,
      int maxComments, BaseRequestWithStore.StoreCredentials storecredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final Body body = new Body(storeName, packageName, maxReviews, maxComments,
        ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences), storecredentials);
    return new ListReviewsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  /**
   * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/sub_limit/0/limit/3
   */
  public static ListReviewsRequest ofTopReviews(String storeName, String packageName,
      int maxReviews, BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return of(storeName, packageName, maxReviews, 0, storeCredentials, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<ListReviews> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listReviews(body, true);
  }

  public static class Body extends BaseBodyWithStore implements Endless {

    private int offset;
    private Integer limit;
    private boolean refresh;
    private String lang;
    private boolean mature;
    private Order order;
    private Sort sort;
    private Long storeId;
    private Long reviewId;
    private String packageName;
    private String storeName;
    private Integer subLimit;

    public Body(long storeId, int limit, int subLimit, boolean refresh,
        BaseRequestWithStore.StoreCredentials storeCredentials) {
      super(storeCredentials);
      this.storeId = storeId;
      this.limit = limit;
      this.subLimit = subLimit;
      this.refresh = refresh;
    }

    public Body(String storeName, String packageName, int limit, int subLimit, boolean refresh,
        BaseRequestWithStore.StoreCredentials storeCredentials) {
      super(storeCredentials);
      this.packageName = packageName;
      this.storeName = storeName;
      this.limit = limit;
      this.subLimit = subLimit;
      this.refresh = refresh;
    }

    @Override public String getLang() {
      return lang;
    }

    @Override public void setLang(String lang) {
      this.lang = lang;
    }

    @Override public boolean isMature() {
      return mature;
    }

    @Override public void setMature(boolean mature) {
      this.mature = mature;
    }

    public Order getOrder() {
      return order;
    }

    public void setOrder(Order order) {
      this.order = order;
    }

    public Sort getSort() {
      return sort;
    }

    public void setSort(Sort sort) {
      this.sort = sort;
    }

    @Override public Long getStoreId() {
      return storeId;
    }

    public void setStoreId(Long storeId) {
      this.storeId = storeId;
    }

    @Override public String getStoreName() {
      return storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public Long getReviewId() {
      return reviewId;
    }

    public void setReviewId(Long reviewId) {
      this.reviewId = reviewId;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public Integer getSubLimit() {
      return subLimit;
    }

    public void setSubLimit(Integer subLimit) {
      this.subLimit = subLimit;
    }

    public boolean isRefresh() {
      return refresh;
    }

    public void setRefresh(boolean refresh) {
      this.refresh = refresh;
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

    public void setLimit(Integer limit) {
      this.limit = limit;
    }

    public enum Sort {
      latest, points
    }
  }
}
