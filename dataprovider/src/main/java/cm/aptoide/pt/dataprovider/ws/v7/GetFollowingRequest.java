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

  protected GetFollowingRequest(GetFollowersRequest.Body body, String baseHost) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
  }

  public static GetFollowingRequest of(BodyInterceptor bodyInterceptor, @Nullable Long userId,
      @Nullable Long storeId) {
    GetFollowersRequest.Body body = new GetFollowersRequest.Body();
    body.setUserId(userId);
    body.setStoreId(storeId);
    return new GetFollowingRequest(((GetFollowersRequest.Body) bodyInterceptor.intercept(body)),
        BASE_HOST);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineGetFollowing(body, bypassCache);
  }
}
