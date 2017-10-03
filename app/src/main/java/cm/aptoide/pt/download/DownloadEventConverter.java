package cm.aptoide.pt.download;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Data;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 05/01/2017.
 */

public class DownloadEventConverter extends DownloadInstallEventConverter<DownloadEvent> {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final NavigationTracker navigationTracker;

  public DownloadEventConverter(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
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

  @Override protected Data convertSpecificFields(DownloadEvent report, Data data) {
    data.getApp()
        .setMirror(report.getMirrorApk());
    for (int i = 0; data.getObb() != null && i < data.getObb()
        .size(); i++) {
      if (i == 0) {
        data.getObb()
            .get(0)
            .setMirror(report.getMirrorObbMain());
      } else {
        data.getObb()
            .get(1)
            .setMirror(report.getMirrorObbPatch());
      }
    }
    return data;
  }

  @Override protected DownloadEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    return new DownloadEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
        versionCode, this, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, navigationTracker.getPreviousViewName());
  }
}
