package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
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

  public InstallEventConverter(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  @Override
  protected DownloadInstallAnalyticsBaseBody.Data convertSpecificFields(InstallEvent report,
      DownloadInstallAnalyticsBaseBody.Data data) {
    DownloadInstallAnalyticsBaseBody.Root root = new DownloadInstallAnalyticsBaseBody.Root();
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
            versionCode, this, bodyInterceptor, httpClient, converterFactory);
    installEvent.setAptoideSettings(ManagerPreferences.allowRootInstallation());
    return installEvent;
  }
}
