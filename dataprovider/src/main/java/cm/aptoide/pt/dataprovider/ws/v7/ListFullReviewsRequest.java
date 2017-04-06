/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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

  protected ListFullReviewsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public ListFullReviewsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static ListFullReviewsRequest of(long storeId, int limit, int offset,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {

    Body body = new Body(storeId, limit, offset, ManagerPreferences.getAndResetForceServerRefresh(),
        storeCredentials);
    return new ListFullReviewsRequest(body, bodyInterceptor);
  }

  public static ListFullReviewsRequest ofAction(String url, boolean refresh,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    return new ListFullReviewsRequest(url.replace("listFullReviews", ""),
        new Body(refresh, storeCredentials), bodyInterceptor);
  }

  @Override protected Observable<ListFullReviews> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (TextUtils.isEmpty(url)) {
      return interfaces.listFullReviews(body, bypassCache);
    } else {
      return interfaces.listFullReviews(url, body, bypassCache);
    }
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBodyWithStore implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    private String lang;
    private boolean mature;
    private String q = Api.Q;
    @Getter private boolean refresh;

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

    public enum Sort {
      latest, points
    }
  }
}
