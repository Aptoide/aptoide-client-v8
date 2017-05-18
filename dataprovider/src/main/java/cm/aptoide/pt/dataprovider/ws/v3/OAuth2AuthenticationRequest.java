/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.OAuth;
import cm.aptoide.pt.preferences.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true)
public class OAuth2AuthenticationRequest extends V3<OAuth> {

  public OAuth2AuthenticationRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor);
  }

  public static OAuth2AuthenticationRequest of(String username, String password, String mode,
      @Nullable String nameForGoogle, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {

    final BaseBody body = new BaseBody();

    body.put("grant_type", "password");
    body.put("client_id", "Aptoide");
    body.put("mode", "json");

    if (mode != null) {
      switch (mode) {
        case "APTOIDE":
          body.put("username", username);
          body.put("password", password);
          break;
        case "GOOGLE":
          body.put("authMode", "google");
          body.put("oauthUserName", nameForGoogle);
          body.put("oauthToken", password);
          break;
        case "FACEBOOK":
          body.put("authMode", "facebook");
          body.put("oauthToken", password);
          break;
        case "ABAN":
          body.put("oauthUserName", username);
          body.put("oauthToken", password);
          body.put("authMode", "aban");
          body.put("oauthUser", nameForGoogle);
          break;
      }
    }

    if (!TextUtils.isEmpty(Application.getConfiguration()
        .getExtraId())) {
      body.put("oem_id", Application.getConfiguration()
          .getExtraId());
    }

    return new OAuth2AuthenticationRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  public static OAuth2AuthenticationRequest of(String refreshToken,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {

    final BaseBody body = new BaseBody();

    body.put("grant_type", "refresh_token");
    body.put("client_id", "Aptoide");
    body.put("mode", "json");

    if (!TextUtils.isEmpty(Application.getConfiguration()
        .getExtraId())) {
      body.put("oem_id", Application.getConfiguration()
          .getExtraId());
    }
    body.put("refresh_token", refreshToken);

    return new OAuth2AuthenticationRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  @Override
  protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.oauth2Authentication(map, bypassCache);
  }
}
