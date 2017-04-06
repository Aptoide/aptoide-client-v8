package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEventConverter extends DownloadInstallEventConverter<InstallEvent> {

  private BodyInterceptor<BaseBody> bodyInterceptor;

  public InstallEventConverter(BodyInterceptor<BaseBody> bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  @Override
  protected DownloadInstallAnalyticsBaseBody.Data convertSpecificFields(InstallEvent report,
      DownloadInstallAnalyticsBaseBody.Data data) {
    return data;
  }

  @Override protected InstallEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    return new InstallEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
        versionCode, this, bodyInterceptor);
  }
}
