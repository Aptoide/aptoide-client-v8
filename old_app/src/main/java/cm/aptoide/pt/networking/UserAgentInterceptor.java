package cm.aptoide.pt.networking;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import cm.aptoide.pt.crashreports.CrashReport;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

  private final IdsRepository idsRepository;
  private final String oemid;
  private final DisplayMetrics displayMetrics;
  private final String terminalInfo;
  private final String versionName;

  public UserAgentInterceptor(IdsRepository idsRepository, String oemid,
      DisplayMetrics displayMetrics, String terminalInfo, String versionName) {
    this.idsRepository = idsRepository;
    this.oemid = oemid;
    this.displayMetrics = displayMetrics;
    this.terminalInfo = terminalInfo;
    this.versionName = versionName;
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

    final StringBuilder sb =
        new StringBuilder(versionName + ";" + terminalInfo + ";" + screen + ";id:");

    String uniqueIdentifier = idsRepository.getUniqueIdentifier()
        .toBlocking()
        .value();
    if (uniqueIdentifier != null) {
      sb.append(uniqueIdentifier);
    }
    sb.append(";");
    sb.append(";");
    if (!TextUtils.isEmpty(oemid)) {
      sb.append(oemid);
    }
    return sb.toString();
  }
}
