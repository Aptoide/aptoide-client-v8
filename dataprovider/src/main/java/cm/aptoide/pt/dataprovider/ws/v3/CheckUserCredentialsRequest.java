/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CheckUserCredentialsRequest extends V3<CheckUserCredentialsJson> {

  private static final String CREATE_REPO_VALUE = "1";
  private static final String OAUTH_CREATE_REPO_VALUE = "true";
  private static final String DEFAULT_AUTH_MODE = "aptoide";
  
  private final boolean createStore;

  private CheckUserCredentialsRequest(BaseBody baseBody, boolean createStore,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
    this.createStore = createStore;
  }

  public static CheckUserCredentialsRequest toCreateStore(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, String storeName) {

    final BaseBody body = new BaseBody();
    body.put("createRepo", CREATE_REPO_VALUE);
    body.put("oauthCreateRepo", OAUTH_CREATE_REPO_VALUE);
    body.put("repo", storeName);
    body.setAuthMode(DEFAULT_AUTH_MODE);

    return new CheckUserCredentialsRequest(body, true, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<CheckUserCredentialsJson> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    if (createStore) {
      return service.checkUserCredentials(map, bypassCache);
    }

    return service.getUserInfo(map, bypassCache);
  }
}
