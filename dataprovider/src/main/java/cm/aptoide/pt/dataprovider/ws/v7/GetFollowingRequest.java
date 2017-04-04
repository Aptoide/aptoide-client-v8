package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.Nullable;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GetFollowingRequest extends V7<GetFollowers, GetFollowersRequest.Body> {

  protected GetFollowingRequest(GetFollowersRequest.Body body,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static GetFollowingRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      @Nullable Long userId,
      @Nullable Long storeId) {
    GetFollowersRequest.Body body = new GetFollowersRequest.Body();
    body.setUserId(userId);
    body.setStoreId(storeId);
    return new GetFollowingRequest(body, bodyInterceptor);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineGetFollowing(body, bypassCache);
  }
}
