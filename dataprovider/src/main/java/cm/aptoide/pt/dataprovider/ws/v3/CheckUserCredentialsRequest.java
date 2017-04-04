/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

public class CheckUserCredentialsRequest extends V3<CheckUserCredentialsJson> {

  private final boolean createStore;

  public CheckUserCredentialsRequest(BaseBody baseBody, boolean createStore,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.createStore = createStore;
  }

  public static CheckUserCredentialsRequest of(String store, String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {

    final BaseBody body = new BaseBody();
    body.put("access_token", accessToken);
    body.put("mode", "json");
    body.put("createRepo", "1");
    body.put("repo", store);
    body.put("authMode", "aptoide");
    body.put("oauthToken", accessToken);
    body.put("oauthCreateRepo", "true");

    return new CheckUserCredentialsRequest(body, true, bodyInterceptor);
  }

  public static CheckUserCredentialsRequest of(String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody body = new BaseBody();
    body.put("access_token", accessToken);
    body.put("mode", "json");
    return new CheckUserCredentialsRequest(body, false, bodyInterceptor);
  }

  @Override
  protected Observable<CheckUserCredentialsJson> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (createStore) {
      return interfaces.checkUserCredentials(map, bypassCache);
    }

    return interfaces.getUserInfo(map, bypassCache);
  }
}
