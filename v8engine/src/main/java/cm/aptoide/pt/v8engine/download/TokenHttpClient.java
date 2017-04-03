/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/09/2016.
 */

package cm.aptoide.pt.v8engine.download;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.downloadmanager.Constants;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.networkclient.okhttp.UserAgentInterceptor;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEvent;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by marcelobenites on 9/12/16.
 */
public class TokenHttpClient {

  private static final String TAG = "TokenHttpClient";

  private final AptoideClientUUID aptoideClientUUID;
  private final UserData userData;
  private final String oemid;
  private final AptoideAccountManager accountManager;

  public TokenHttpClient(AptoideClientUUID aptoideClientUUID, UserData userData, String oemid,
      AptoideAccountManager accountManager) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.userData = userData;
    this.oemid = oemid;
    this.accountManager = accountManager;
  }

  public OkHttpClient.Builder customMake() {
    return new OkHttpClient.Builder().addInterceptor(chain -> {
      Request request = chain.request();

      // Paid apps URLs are actually web services. We need to add token information in order
      // to validate user is allowed to download the app.
      if (request.url().host().contains(BuildConfig.APTOIDE_WEB_SERVICES_HOST)) {
        request = request.newBuilder()
            .post(RequestBody.create(MediaType.parse("application/json"),
                "{\"access_token\" : \"" + accountManager.getAccessToken() + "\"}"))
            .build();
      }
      return chain.proceed(request);
    })
        .addInterceptor(new UserAgentInterceptor(
            () -> AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, userData,
                AptoideUtils.Core.getDefaultVername(), oemid, AptoideUtils.SystemU.TERMINAL_INFO)))

        .addInterceptor(new Interceptor() {
          @Override public Response intercept(Chain chain) throws IOException {


        /*
         * Aptoide - events 2 : download
         * Get X-Mirror and add to the event
         */

            Request request = chain.request();
            String v = request.header(Constants.VERSION_CODE);
            String packageName = request.header(Constants.PACKAGE);
            int fileType = Integer.parseInt(request.header(Constants.FILE_TYPE));

            Response response = chain.proceed(request.newBuilder()
                .removeHeader(Constants.VERSION_CODE)
                .removeHeader(Constants.PACKAGE)
                .removeHeader(Constants.FILE_TYPE)
                .build());
            if (response != null) {
              Headers allHeaders = response.headers();
              if (allHeaders != null) {
                String mirror = allHeaders.get("X-Mirror");
                addMirrorToDownloadEvent(v, packageName, fileType, mirror);
              }
            }
            return response;
          }
        });
  }

  private void addMirrorToDownloadEvent(String v, String packageName, int fileType,
      String headerValue) {
    DownloadEvent event =
        (DownloadEvent) Analytics.getInstance().get(packageName + v, DownloadEvent.class);
    if (event != null) {
      if (fileType == 0) {
        event.setMirrorApk(headerValue);
      } else if (fileType == 1) {
        event.setMirrorObbMain(headerValue);
      } else if (fileType == 2) {
        event.setMirrorObbPatch(headerValue);
      }
    }
  }
}
