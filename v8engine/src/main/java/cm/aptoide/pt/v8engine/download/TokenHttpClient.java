/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/09/2016.
 */

package cm.aptoide.pt.v8engine.download;

import cm.aptoide.accountmanager.AptoideAccountManager;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by marcelobenites on 9/12/16.
 */
public class TokenHttpClient implements FileDownloadHelper.OkHttpClientCustomMaker {

  @Override public OkHttpClient customMake() {
    return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        // Paid apps URLs are actually web services. We need to add token information in order
        // to validate user is allowed to download the app.
        if (request.url().host().contains("webservices.aptoide.com")) {
          request = request.newBuilder()
              .post(RequestBody.create(MediaType.parse("application/json"),
                  "{\"access_token\" : \"" + AptoideAccountManager.getAccessToken() + "\"}"))
              .build();
        }

        return chain.proceed(request);
      }
    }).build();
  }
}
