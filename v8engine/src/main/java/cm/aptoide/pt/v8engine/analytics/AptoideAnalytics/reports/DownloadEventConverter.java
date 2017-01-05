package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

/**
 * Created by trinkes on 05/01/2017.
 */

public class DownloadEventConverter extends DownloadInstallEventConverter<DownloadEvent> {

  @Override protected DownloadEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    return new DownloadEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
        versionCode, this);
  }
}
