package cm.aptoide.pt.networkclient.okhttp;

import android.text.TextUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

  private final UserAgentGenerator userAgentGenerator;

  public UserAgentInterceptor(UserAgentGenerator userAgentGenerator) {
    this.userAgentGenerator = userAgentGenerator;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();

    String userAgent = null;
    try {
      userAgent = userAgentGenerator.generateUserAgent();
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
    }

    Response response;
    try {
      if (!TextUtils.isEmpty(userAgent)) {
        Request requestWithUserAgent =
            originalRequest.newBuilder().header("User-Agent", userAgent).build();
        response = chain.proceed(requestWithUserAgent);
      } else {
        response = chain.proceed(originalRequest);
      }
      return response;
    } catch (IOException e) {
      // something bad happened if we reached here.
      CrashReport.getInstance().log(e);
      throw e;
    }
  }
}
