package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.GetFollowers;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GetFollowersRequest extends V7<GetFollowers, GetFollowersRequest.Body> {
  protected GetFollowersRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static GetFollowersRequest of(BodyInterceptor bodyInterceptor, Long userId) {
    Body body = new Body();
    body.setUserId(userId);
    return new GetFollowersRequest(((Body) bodyInterceptor.intercept(body)), BASE_HOST);
  }

  public static GetFollowersRequest ofStore(BodyInterceptor bodyInterceptor, Long storeId) {
    Body body = new Body();
    body.setStoreId(storeId);
    return new GetFollowersRequest(((Body) bodyInterceptor.intercept(body)), BASE_HOST);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineFollowers(body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody
      implements Endless {

    private int limit = 25;
    private int offset;
    @Setter @Getter private Long userId;
    @Setter @Getter private Long storeId;

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
