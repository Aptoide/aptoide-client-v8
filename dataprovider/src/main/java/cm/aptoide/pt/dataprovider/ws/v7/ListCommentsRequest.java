package cm.aptoide.pt.dataprovider.ws.v7;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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

  public static ListCommentsRequest ofAction(String url, boolean refresh,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUUID) {
    ListCommentsRequest.url = url;
    String username = storeCredentials.getUsername();
    String password = storeCredentials.getPasswordSha1();
    Body body = new Body(refresh, Order.desc);
    body.setStore_user(username);
    body.setStore_pass_sha1(password);
    // FIXME: 30/11/2016 sithengineer remove the next two line comments
    //body.setStoreId(storeCredentials.getId());
    //body.defineCommentType(CommentType.STORE);

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static ListCommentsRequest of(String url, long reviewId, int limit,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUUID) {
    ListCommentsRequest.url = url;
    return of(reviewId, limit, storeCredentials, accessToken, aptoideClientUUID);
  }

  public static ListCommentsRequest of(long reviewId, int offset, int limit, String accessToken,
      String aptoideClientUUID) {
    ListCommentsRequest listCommentsRequest = of(reviewId, limit, accessToken, aptoideClientUUID);
    listCommentsRequest.getBody().setOffset(offset);
    return listCommentsRequest;
  }

  public static ListCommentsRequest of(long reviewId, int limit, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(limit, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc);
    body.setReviewId(reviewId);
    return new ListCommentsRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static ListCommentsRequest of(long reviewId, int limit,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUUID) {
    String username = storeCredentials.getUsername();
    String password = storeCredentials.getPasswordSha1();
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    Body body =
        new Body(limit, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc, username,
            password);
    body.setReviewId(reviewId);
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

  @Data @Accessors @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody implements Endless {

    @Getter @Setter private int offset;
    @Getter private Integer limit;
    @Getter private boolean refresh;
    private String q = Api.Q;
    private Order order;
    private String comment_type = CommentType.REVIEW.name();
    private Long reviewId;
    private Long storeId;
    private String store_user;
    private String store_pass_sha1;

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

    void defineCommentType(CommentType commentType){
      this.comment_type = commentType.name();
    }
  }

  enum CommentType {
    REVIEW, STORE
  }
}
