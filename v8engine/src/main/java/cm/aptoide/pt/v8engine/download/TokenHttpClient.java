/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/09/2016.
 */

package cm.aptoide.pt.v8engine.download;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.networkclient.okhttp.UserAgentInterceptor;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by marcelobenites on 9/12/16.
 */
public class TokenHttpClient {

  private final AptoideClientUUID aptoideClientUUID;
  private final UserData userData;
  private final String oemid;

  public TokenHttpClient(AptoideClientUUID aptoideClientUUID, UserData userData, String oemid) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.userData = userData;
    this.oemid = oemid;
  }

  public OkHttpClient.Builder customMake() {
    return new OkHttpClient.Builder().addInterceptor(chain -> {
      Request request = chain.request();

      // Paid apps URLs are actually web services. We need to add token information in order
      // to validate user is allowed to download the app.
      if (request.url().host().contains(BuildConfig.APTOIDE_WEB_SERVICES_HOST)) {
        request = request.newBuilder()
            .post(RequestBody.create(MediaType.parse("application/json"),
                "{\"access_token\" : \"" + AptoideAccountManager.getAccessToken() + "\"}"))
            .build();
      }
      return chain.proceed(request);
    })
        .addInterceptor(new UserAgentInterceptor(
            () -> AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, userData,
                AptoideUtils.Core.getDefaultVername(), oemid)));
  }
}
