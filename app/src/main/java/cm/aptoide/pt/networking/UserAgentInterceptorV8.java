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
  private final AuthenticationPersistence authenticationPersistence;
  private final String androidVersion;
  private final int apiLevel;
  private final String model;
  private final String productCode;
  private final String architecture;

  public UserAgentInterceptorV8(IdsRepository idsRepository, String androidVersion, int apiLevel,
      String model, String productCode, String architecture, DisplayMetrics displayMetrics,
      String versionName, String aptoidePackage, String aptoideMd5, int aptoideVersionCode,
      AuthenticationPersistence authenticationPersistence) {
    this.idsRepository = idsRepository;
    this.androidVersion = androidVersion;
    this.apiLevel = apiLevel;
    this.model = model;
    this.productCode = productCode;
    this.architecture = architecture;
    this.displayMetrics = displayMetrics;
    this.versionName = versionName;
    this.aptoidePackage = aptoidePackage;
    this.aptoideMd5 = aptoideMd5;
    this.aptoideVersionCode = aptoideVersionCode;
    this.authenticationPersistence = authenticationPersistence;
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
      Authentication authentication = authenticationPersistence.getAuthentication()
          .toBlocking()
          .value();
      Request.Builder requestBuilder = originalRequest.newBuilder();
      if (authentication.isAuthenticated() || !TextUtils.isEmpty(userAgent)) {
        if (authentication.isAuthenticated()) {
          String accessToken = authentication.getAccessToken();
          requestBuilder.header("AUTHORIZATION", accessToken);
        }
        if (!TextUtils.isEmpty(userAgent)) {
          requestBuilder.header("User-Agent", userAgent);
        }
        Request requestWithUserAgent = requestBuilder.build();
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
        + apiLevel
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
