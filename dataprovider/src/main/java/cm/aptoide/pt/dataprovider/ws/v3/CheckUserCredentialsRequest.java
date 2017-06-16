/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CheckUserCredentialsRequest extends V3<CheckUserCredentialsJson> {

  private CheckUserCredentialsRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static CheckUserCredentialsRequest of(String storeName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    final BaseBody body = new BaseBody();
    body.put("mode", "json");
    body.put("createRepo", "1");
    body.put("repo", storeName);
    body.put("authMode", "aptoide");
    body.put("oauthCreateRepo", "true");

    return new CheckUserCredentialsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<CheckUserCredentialsJson> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.checkUserCredentials(map, bypassCache);
  }
}
