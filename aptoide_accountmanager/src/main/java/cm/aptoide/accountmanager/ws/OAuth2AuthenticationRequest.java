/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import java.util.HashMap;
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
public class OAuth2AuthenticationRequest extends v3accountManager<OAuth> {

  private String username;
  private String password;
  private LoginMode mode;
  private String nameForGoogle;
  private String grantType;
  private String refreshToken;

  public OAuth2AuthenticationRequest(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(httpClient, converterFactory);
  }

  public static OAuth2AuthenticationRequest of(String username, String password, LoginMode mode,
      @Nullable String nameForGoogle) {
    return new OAuth2AuthenticationRequest(OkHttpClientFactory.getSingletonClient(),
        WebService.getDefaultConverter()).setUsername(username)
        .setPassword(password)
        .setMode(mode)
        .setGrantType("password")
        .setNameForGoogle(nameForGoogle);
  }

  public static OAuth2AuthenticationRequest of(String refreshToken) {

    return new OAuth2AuthenticationRequest(OkHttpClientFactory.getSingletonClient(),
        WebService.getDefaultConverter()).setGrantType("refresh_token")
        .setRefreshToken(refreshToken);
  }

  @Override
  protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    HashMap<String, String> parameters = new HashMap<>();

    parameters.put("grant_type", grantType);
    parameters.put("client_id", "Aptoide");
    parameters.put("mode", "json");

    if (mode != null) {
      switch (mode) {
        case APTOIDE:
          parameters.put("username", username);
          parameters.put("password", password);
          break;
        case GOOGLE:
          parameters.put("authMode", "google");
          parameters.put("oauthUserName", nameForGoogle);
          parameters.put("oauthToken", password);
          break;
        case FACEBOOK:
          parameters.put("authMode", "facebook");
          parameters.put("oauthToken", password);
          break;
      }
    }
    if (refreshToken != null) {
      parameters.put("refresh_token", refreshToken);
    }

    //		// TODO: 25-04-2016 neuro oemId :)
    //		if(Aptoide.getConfiguration().getExtraId().length()>0){
    //			parameters.put("oem_id", Aptoide.getConfiguration().getExtraId());
    //		}

    return interfaces.oauth2Authentication(parameters);
  }
}
