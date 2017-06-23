/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.ListFullReviews;
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
public class ListFullReviewsRequest extends V7<ListFullReviews, ListFullReviewsRequest.Body> {

  private static final int MAX_REVIEWS = 10;
  private static final int MAX_COMMENTS = 10;
  private String url;

  protected ListFullReviewsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public ListFullReviewsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this(body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
    this.url = url;
  }

  public static ListFullReviewsRequest of(long storeId, int limit, int offset,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    Body body = new Body(storeId, limit, offset,
        ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences), storeCredentials);
    return new ListFullReviewsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static ListFullReviewsRequest ofAction(String url, boolean refresh,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new ListFullReviewsRequest(url.replace("listFullReviews", ""),
        new Body(refresh, storeCredentials), bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<ListFullReviews> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (TextUtils.isEmpty(url)) {
      return interfaces.listFullReviews(body, bypassCache);
    } else {
      return interfaces.listFullReviews(url, body, bypassCache);
    }
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
    public Body(boolean refresh, BaseRequestWithStore.StoreCredentials storeCredentials) {
      super(storeCredentials);
      this.refresh = refresh;
    }
    public Body(long storeId, int limit, int offset, boolean refresh,
        BaseRequestWithStore.StoreCredentials storeCredentials) {
      super(storeCredentials);
      this.storeId = storeId;
      this.limit = limit;
      this.offset = offset;
      this.refresh = refresh;
    }
    public Body(String storeName, String packageName, int limit, int subLimit, boolean refresh) {

      this.packageName = packageName;
      this.storeName = storeName;
      this.limit = limit;
      this.subLimit = subLimit;
      this.refresh = refresh;
    }

    public boolean isRefresh() {
      return refresh;
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

    public enum Sort {
      latest, points
    }
  }
}
