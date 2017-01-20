package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEvent extends DownloadInstallBaseEvent {
  private static final String EVENT_NAME = "INSTALL";

  public InstallEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadInstallEventConverter downloadInstallEventConverter) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME);
  }
}
