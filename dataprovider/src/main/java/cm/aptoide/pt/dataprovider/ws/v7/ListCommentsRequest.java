package cm.aptoide.pt.dataprovider.ws.v7;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import rx.Observable;

/**
 * http://ws2.aptoide.com/api/7/listFullComments/info/1
 * <p>
 * http://ws2.aptoide.com/api/7/listComments/info/1
 */
public class ListCommentsRequest extends V7<ListComments, ListCommentsRequest.Body> {

  private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";
  private static String url;

  private ListCommentsRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static ListCommentsRequest ofStoreAction(String url, boolean refresh,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUuid) {

    ListCommentsRequest.url = url;

    Body body = new Body(refresh, Order.desc);
    body.setStore_user(storeCredentials.getUsername());
    body.setStore_pass_sha1(storeCredentials.getPasswordSha1());
    body.setStoreId(storeCredentials.getId());

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuid);
    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static ListCommentsRequest of(String url, long resourceId, int limit,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUuid, boolean isReview) {
    ListCommentsRequest.url = url;
    return of(resourceId, limit, storeCredentials, accessToken, aptoideClientUuid, isReview);
  }

  public static ListCommentsRequest of(long resourceId, int offset, int limit, String accessToken,
      String aptoideClientUuid, boolean isReview) {
    ListCommentsRequest listCommentsRequest = of(resourceId, limit, accessToken, aptoideClientUuid, isReview);
    listCommentsRequest.getBody().setOffset(offset);
    return listCommentsRequest;
  }

  public static ListCommentsRequest of(long resourceId, int limit, String accessToken,
      String aptoideClientUuid, boolean isReview) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuid);
    Body body = new Body(limit, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc);

    if(isReview) {
      body.setReviewId(resourceId);
    } else{
      body.setStoreId(resourceId);
    }

    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static ListCommentsRequest of(long resourceId, int limit,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUuid, boolean isReview) {
    String username = storeCredentials.getUsername();
    String password = storeCredentials.getPasswordSha1();
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuid);

    Body body =
        new Body(limit, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc, username,
            password);

    if(isReview) {
      body.setReviewId(resourceId);
    } else{
      body.setStoreId(resourceId);
    }

    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static ListCommentsRequest ofTimeline(String url, boolean refresh, String timelineArticleId,
      String accessToken, String aptoideClientUuid) {

    ListCommentsRequest.url = url;

    Body body = new Body(refresh, Order.desc);
    body.setTimelineArticleId(timelineArticleId);

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuid);
    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<ListComments> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    //bypassCache is not used, for comments always get new data
    if (TextUtils.isEmpty(url)) {
      return interfaces.listComments(body, true);
    } else {
      return interfaces.listComments(url, body, true);
    }
  }

  public static class Body extends BaseBody implements Endless {

    private int offset;
    private Integer limit;
    private boolean refresh;
    private String q = Api.Q;
    private Order order;
    private String commentType = CommentType.REVIEW.name();
    private Long reviewId;
    private Long storeId;
    private long subLimit = 5;
    private String store_user;
    private String store_pass_sha1;
    private String timelineArticleId;

    public Body(boolean refresh, Order order) {
      this.refresh = refresh;
      this.order = order;
    }

    public Body(int limit, boolean refresh, Order order) {
      this.limit = limit;
      this.refresh = refresh;
      this.order = order;
    }

    public Body(int limit, boolean refresh, Order order, String username, String password) {
      this.limit = limit;
      this.refresh = refresh;
      this.order = order;
      this.store_user = username;
      this.store_pass_sha1 = password;
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

    public boolean isRefresh() {
      return refresh;
    }

    public void setRefresh(boolean refresh) {
      this.refresh = refresh;
    }

    @Override public String getQ() {
      return q;
    }

    @Override public void setQ(String q) {
      this.q = q;
    }

    public Order getOrder() {
      return order;
    }

    public void setOrder(Order order) {
      this.order = order;
    }

    public String getCommentType() {
      return commentType;
    }

    public Long getReviewId() {
      return reviewId;
    }

    public void setReviewId(Long reviewId) {
      this.reviewId = reviewId;
      commentType = CommentType.REVIEW.name();
    }

    public Long getStoreId() {
      return storeId;
    }

    public void setStoreId(Long storeId) {
      this.storeId = storeId;
      commentType = CommentType.STORE.name();
    }

    public String getStore_user() {
      return store_user;
    }

    public void setStore_user(String store_user) {
      this.store_user = store_user;
    }

    public String getStore_pass_sha1() {
      return store_pass_sha1;
    }

    public void setStore_pass_sha1(String store_pass_sha1) {
      this.store_pass_sha1 = store_pass_sha1;
    }

    public long getSubLimit() {
      return subLimit;
    }

    public void setSubLimit(long subLimit) {
      this.subLimit = subLimit;
    }

    public void setTimelineArticleId(String timelineArticleId) {
      this.timelineArticleId = timelineArticleId;
    }

    public String getTimelineArticleId() {
      return timelineArticleId;
    }
  }
}
