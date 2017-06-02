/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static ListReviewsRequest of(String storeName, String packageName,
      BaseRequestWithStore.StoreCredentials storecredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return of(storeName, packageName, MAX_REVIEWS, MAX_COMMENTS, storecredentials, bodyInterceptor,
        httpClient, converterFactory);
  }

  /**
   * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
   */
  public static ListReviewsRequest of(String storeName, String packageName, int maxReviews,
      int maxComments, BaseRequestWithStore.StoreCredentials storecredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final Body body = new Body(storeName, packageName, maxReviews, maxComments,
        ManagerPreferences.getAndResetForceServerRefresh(), storecredentials);
    return new ListReviewsRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  /**
   * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/sub_limit/0/limit/3
   */
  public static ListReviewsRequest ofTopReviews(String storeName, String packageName,
      int maxReviews, BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return of(storeName, packageName, maxReviews, 0, storeCredentials, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<ListReviews> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listReviews(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBodyWithStore implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    private String lang;
    private boolean mature;
    @Getter private boolean refresh;

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

    public enum Sort {
      latest, points
    }
  }
}
