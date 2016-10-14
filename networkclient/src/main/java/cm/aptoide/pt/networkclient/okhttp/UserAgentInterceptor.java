package cm.aptoide.pt.networkclient.okhttp;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sithengineer on 13/10/2016.
 */

public class UserAgentInterceptor implements Interceptor {

  private final String userAgent;

  public UserAgentInterceptor(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request requestWithUserAgent =
        originalRequest.newBuilder().header("User-Agent", userAgent).build();
    return chain.proceed(requestWithUserAgent);
  }
}
