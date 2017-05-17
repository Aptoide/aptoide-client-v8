package cm.aptoide.pt.v8engine.networking;

import android.accounts.Account;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

  private final AndroidAccountProvider androidAccountProvider;
  private final IdsRepository idsRepository;
  private final String oemid;
  private final DisplayMetrics displayMetrics;
  private final String terminalInfo;
  private final String versionName;

  public UserAgentInterceptor(AndroidAccountProvider androidAccountProvider,
      IdsRepository idsRepository, String oemid, DisplayMetrics displayMetrics, String terminalInfo,
      String versionName) {
    this.androidAccountProvider = androidAccountProvider;
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

  public String getDefaultUserAgent() {

    String myscr = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;

    final StringBuilder sb =
        new StringBuilder(versionName + ";" + terminalInfo + ";" + myscr + ";id:");

    String uniqueIdentifier = idsRepository.getUniqueIdentifier();
    if (uniqueIdentifier != null) {
      sb.append(uniqueIdentifier);
    }
    sb.append(";");

    final Account account = androidAccountProvider.getAndroidAccount()
        .onErrorReturn(throwable -> null)
        .toBlocking()
        .value();

    if (account != null && account.name != null) {
      sb.append(account.name);
    }
    sb.append(";");
    if (!TextUtils.isEmpty(oemid)) {
      sb.append(oemid);
    }
    return sb.toString();
  }
}
