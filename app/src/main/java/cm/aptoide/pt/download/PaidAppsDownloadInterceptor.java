package cm.aptoide.pt.download;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaidAppsDownloadInterceptor implements Interceptor {

  private final AptoideAccountManager accountManager;

  public PaidAppsDownloadInterceptor(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    // Paid apps URLs are actually web services. We need to add token information in order
    // to validate user is allowed to download the app.
    if (request.url()
        .host()
        .contains(BuildConfig.APTOIDE_WEB_SERVICES_HOST)) {
      final Account account = accountManager.accountStatus()
          .first()
          .toSingle()
          .toBlocking()
          .value();
      if (account.isLoggedIn()) {
        request = request.newBuilder()
            .post(RequestBody.create(MediaType.parse("application/json"),
                "{\"access_token\" : \"" + account.getAccessToken() + "\"}"))
            .build();
      }
    }

    return chain.proceed(request);
  }
}
