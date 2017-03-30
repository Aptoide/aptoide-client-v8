/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.GetUserRepoSubscription;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

public class GetUserRepoSubscriptionRequest extends V3<GetUserRepoSubscription> {

  public GetUserRepoSubscriptionRequest(BaseBody baseBody) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
  }

  public static GetUserRepoSubscriptionRequest of(String accessToken) {
    final BaseBody body = new BaseBody();
    body.put("mode", "json");
    body.put("access_token", accessToken);
    return new GetUserRepoSubscriptionRequest(body);
  }

  @Override
  protected Observable<GetUserRepoSubscription> loadDataFromNetwork(V3.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getUserRepos(map);
  }
}
