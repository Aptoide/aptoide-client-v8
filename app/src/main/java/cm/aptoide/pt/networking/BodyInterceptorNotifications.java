package cm.aptoide.pt.networking;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import java.util.Map;
import rx.Single;

/**
 * Created by trinkes on 06/11/2017.
 */
public class BodyInterceptorNotifications implements BodyInterceptor<Map<String, String>> {
  private final AuthenticationPersistence authenticationPersistence;

  public BodyInterceptorNotifications(AuthenticationPersistence authenticationPersistence) {

    this.authenticationPersistence = authenticationPersistence;
  }

  @Override public Single<Map<String, String>> intercept(Map<String, String> body) {
    return authenticationPersistence.getAuthentication()
        .doOnSuccess(authentication -> {
          if (authentication.isAuthenticated()) {
            body.put("access_token", authentication.getAccessToken());
          }
        })
        .map(authentication -> body);
  }
}
