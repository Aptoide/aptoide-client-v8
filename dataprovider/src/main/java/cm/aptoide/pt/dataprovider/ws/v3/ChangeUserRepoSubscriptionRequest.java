/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

public class ChangeUserRepoSubscriptionRequest extends V3<BaseV3Response> {

  public ChangeUserRepoSubscriptionRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static ChangeUserRepoSubscriptionRequest of(String storeName, boolean subscribe,
      String accessToken, BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody body = new BaseBody();
    body.put("mode", "json");
    body.put("repo", storeName);
    body.put("status", subscribe ? "subscribed" : "unsubscribed");
    body.put("access_token", accessToken);
    return new ChangeUserRepoSubscriptionRequest(body, bodyInterceptor);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.changeUserRepoSubscription(map);
  }
}
