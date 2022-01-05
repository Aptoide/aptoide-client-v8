package cm.aptoide.pt.networking;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Pnp1AuthorizationInterceptor implements Interceptor {
  public static final String ACCESS_TOKEN_KEY = "access_token";
  private final AuthenticationPersistence authenticationPersistence;
  private final TokenInvalidator tokenInvalidator;
  private long timeStamp;

  public Pnp1AuthorizationInterceptor(AuthenticationPersistence authenticationPersistence,
      TokenInvalidator tokenInvalidator) {
    this.authenticationPersistence = authenticationPersistence;
    this.tokenInvalidator = tokenInvalidator;
    timeStamp = 0;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl url;
    if (authenticationPersistence.getAuthentication()
        .toBlocking()
        .value()
        .isAuthenticated()) {
      url = request.url()
          .newBuilder()
          .addQueryParameter(ACCESS_TOKEN_KEY, authenticationPersistence.getAuthentication()
              .toBlocking()
              .value()
              .getAccessToken())
          .build();
      request = request.newBuilder()
          .url(url)
          .build();
    }

    Response response = chain.proceed(request);
    if (response.code() == 401) {
      invalidateToken();
      url = request.url()
          .newBuilder()
          .setQueryParameter(ACCESS_TOKEN_KEY, authenticationPersistence.getAuthentication()
              .toBlocking()
              .value()
              .getAccessToken())
          .build();
      Request invalidatedRequest = request.newBuilder()
          .url(url)
          .build();
      response = chain.proceed(invalidatedRequest);
    }

    return response;
  }

  private synchronized void invalidateToken() {
    if (System.currentTimeMillis() - timeStamp > 5000) {
      tokenInvalidator.invalidateAccessToken()
          .await();
      timeStamp = System.currentTimeMillis();
    }
  }
}
