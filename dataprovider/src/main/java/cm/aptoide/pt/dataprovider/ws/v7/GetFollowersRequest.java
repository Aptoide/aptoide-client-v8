package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetFollowers;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GetFollowersRequest extends V7<GetFollowers, GetFollowersRequest.Body> {
  protected GetFollowersRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetFollowersRequest of(BodyInterceptor<BaseBody> bodyInterceptor, Long userId,
      Long storeId, OkHttpClient httpClient, Converter.Factory converterFactory) {
    Body body = new Body();
    body.setUserId(userId);
    body.setStoreId(storeId);
    return new GetFollowersRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  public static GetFollowersRequest ofStore(BodyInterceptor<BaseBody> bodyInterceptor, Long storeId,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    Body body = new Body();
    body.setStoreId(storeId);
    return new GetFollowersRequest(body, bodyInterceptor, httpClient, converterFactory);
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
