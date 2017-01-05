package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEventConverter extends DownloadInstallEventConverter<InstallEvent> {
  @Override protected InstallEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    return new InstallEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
        versionCode, this);
  }
}
