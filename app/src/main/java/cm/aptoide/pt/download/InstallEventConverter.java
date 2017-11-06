package cm.aptoide.pt.download;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Data;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Root;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEventConverter extends DownloadInstallEventConverter<InstallEvent> {

  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final NavigationTracker navigationTracker;

  public InstallEventConverter(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, String appId,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager, NavigationTracker navigationTracker) {
    super(appId, connectivityManager, telephonyManager);
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.navigationTracker = navigationTracker;
  }

  @Override protected Data convertSpecificFields(InstallEvent report, Data data) {
    Root root = new Root();
    root.setAptoideSettings(report.getAptoideSettings());
    root.setPhone(report.getIsPhoneRooted());
    data.setRoot(root);
    return data;
  }

  @Override protected InstallEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    InstallEvent installEvent =
        new InstallEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
            versionCode, this, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences, navigationTracker.getPreviousViewName(),
            navigationTracker.getPreviousScreen() == null ? null
                : navigationTracker.getPreviousScreen()
                    .getStore(), navigationTracker.getCurrentScreen()
            .getTag());
    installEvent.setAptoideSettings(ManagerPreferences.allowRootInstallation(sharedPreferences));
    return installEvent;
  }
}
