package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.GetFollowers;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GetUserLikesRequest extends V7<GetFollowers, GetUserLikesRequest.Body> {
  protected GetUserLikesRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static GetUserLikesRequest of(String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetUserLikesRequest(((Body) decorator.decorate(new Body(), accessToken)), BASE_HOST);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getCardUserLikes(body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody
      implements Endless {

    private int limit = 25;
    private int offset;

    public Body() {
      super();
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
