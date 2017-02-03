/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.content.Context;
import android.os.Build;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.util.AccountManagerUtils;
import cm.aptoide.accountmanager.util.Filters;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.util.Locale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class CheckUserCredentialsRequest
    extends v3accountManager<CheckUserCredentialsJson> {

  private boolean registerDevice;
  private String deviceId;
  private String model;
  private String sdk;
  private String density;
  private String cpu;
  private String screenSize;
  private String openGl;
  private String token;
  private String repoName;
  private String createRepo = ""; // 1 if repo is to be created

  public CheckUserCredentialsRequest(Context context, OkHttpClient httpClient,
      Converter.Factory converterFactory, AptoideAccountManager accountManager) {
    super(httpClient, converterFactory, accountManager);
    deviceId = AccountManagerUtils.getDeviceId(context);
    sdk = String.valueOf(AccountManagerUtils.getSdkVer());
    cpu = AccountManagerUtils.getAbis();
    density = String.valueOf(AccountManagerUtils.getNumericScreenSize(context));
    openGl = AccountManagerUtils.getGlEsVer(context);
    model = Build.MODEL;
    screenSize = Filters.Screen.values()[AccountManagerUtils.getScreenSize(context)].name()
        .toLowerCase(Locale.ENGLISH);
  }

  public static CheckUserCredentialsRequest of(String accessToken, String repoName,
      String createRepo, Context context) {
    CheckUserCredentialsRequest request = of(accessToken, context);
    request.setRepoName(repoName);
    request.setCreateRepo(createRepo);
    return request;
  }

  /**
   * Create a checkUserInfo request
   *
   * @param accessToken access token to identify user
   * @param context
   * @return the built request
   */
  public static CheckUserCredentialsRequest of(String accessToken, Context context) {
    CheckUserCredentialsRequest request = new CheckUserCredentialsRequest(Application.getContext(),
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), isDebug()),
        WebService.getDefaultConverter(), AptoideAccountManager.getInstance(context,
        Application.getConfiguration()));
    request.setToken(accessToken);
    return request;
  }

  @Override
  protected Observable<CheckUserCredentialsJson> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    parameters.put("access_token", token);
    parameters.put("mode", "json");

    if (registerDevice) {
      parameters.put("device_id", deviceId);
      parameters.put("model", model);
      parameters.put("maxSdk", sdk);
      parameters.put("myDensity", density);
      parameters.put("myCpu", cpu);
      parameters.put("maxScreen", screenSize);
      parameters.put("maxGles", openGl);
    }

    if (createRepo.equals("1")) {
      parameters.put("createRepo", createRepo);
      parameters.put("repo", repoName);
      parameters.put("authMode", "aptoide");
      parameters.put("oauthToken", token);
      parameters.put("oauthCreateRepo", "true");
      return interfaces.checkUserCredentials(parameters);
    }

    return interfaces.getUserInfo(parameters);
  }
}
