package cm.aptoide.pt.networking;

import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.preferences.AptoideMd5Manager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

  private final IdsRepository idsRepository;
  private final DisplayMetrics displayMetrics;
  private final AptoideMd5Manager aptoideMd5Manager;

  public UserAgentInterceptor(IdsRepository idsRepository, DisplayMetrics displayMetrics,
      AptoideMd5Manager aptoideMd5Manager) {
    this.idsRepository = idsRepository;
    this.displayMetrics = displayMetrics;
    this.aptoideMd5Manager = aptoideMd5Manager;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();

    String userAgent = null;
    try {
      userAgent = getDefaultUserAgent();
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }

    Response response;
    try {
      if (!TextUtils.isEmpty(userAgent)) {
        Request requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", userAgent)
            .build();
        response = chain.proceed(requestWithUserAgent);
      } else {
        response = chain.proceed(originalRequest);
      }
      return response;
    } catch (IOException e) {
      // something bad happened if we reached here.
      CrashReport.getInstance()
          .log(e);
      throw e;
    }
  }

  private String getDefaultUserAgent() {
    String screen = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
    String uniqueIdentifier = idsRepository.getUniqueIdentifier()
        .toBlocking()
        .value();

    return "Aptoide/" + BuildConfig.VERSION_NAME
        + " (Linux; Android " + Build.VERSION.RELEASE + "; "
        + Build.VERSION.SDK_INT + "; " + Build.MODEL + " "
        + "Build/" + Build.PRODUCT + "; "
        + System.getProperty("os.arch") + "; "
        + BuildConfig.APPLICATION_ID + "; "
        + BuildConfig.VERSION_CODE + "; "
        + aptoideMd5Manager.getAptoideMd5() + "; "
        + screen + ";"
        + uniqueIdentifier + ")";
  }
}
