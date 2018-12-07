package cm.aptoide.pt.networking;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import cm.aptoide.pt.crashreports.CrashReport;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptorV8 implements Interceptor {
  private final IdsRepository idsRepository;
  private final DisplayMetrics displayMetrics;
  private final String versionName;
  private final String aptoidePackage;
  private final String aptoideMd5;
  private final int aptoideVersionCode;
  private final String androidVersion;
  private final String model;
  private final String productCode;
  private final String architecture;

  public UserAgentInterceptorV8(IdsRepository idsRepository, String androidVersion, String model,
      String productCode, String architecture, DisplayMetrics displayMetrics, String versionName,
      String aptoidePackage, String aptoideMd5, int aptoideVersionCode) {
    this.idsRepository = idsRepository;
    this.androidVersion = androidVersion;
    this.model = model;
    this.productCode = productCode;
    this.architecture = architecture;
    this.displayMetrics = displayMetrics;
    this.versionName = versionName;
    this.aptoidePackage = aptoidePackage;
    this.aptoideMd5 = aptoideMd5;
    this.aptoideVersionCode = aptoideVersionCode;
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

  public String getDefaultUserAgent() {

    String myscr = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;

    final StringBuilder sb = new StringBuilder("Aptoide/"
        + versionName
        + " (Linux; Android "
        + androidVersion
        + "; "
        + model
        + " Build/"
        + productCode
        + "; "
        + architecture
        + "; "
        + aptoidePackage
        + "; "
        + aptoideVersionCode
        + "; "
        + aptoideMd5
        + "; "
        + myscr
        + "; ");

    String uniqueIdentifier = idsRepository.getUniqueIdentifier();
    if (uniqueIdentifier != null) {
      sb.append(uniqueIdentifier);
    }
    sb.append(")");

    return sb.toString();
  }
}
