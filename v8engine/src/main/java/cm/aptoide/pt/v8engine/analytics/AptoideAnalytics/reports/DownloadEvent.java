package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import cm.aptoide.pt.logger.Logger;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by trinkes on 02/01/2017.
 */

public @Data @ToString class DownloadEvent extends DownloadInstallBaseEvent {
  public static final String EVENT_NAME = "download";
  /**
   * this variable should be activated when the download progress starts, this will prevent the
   * event to be sent if download was cached
   */
  @Setter private boolean downloadHadProgress;
  private Throwable error;

  public DownloadEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadEventConverter downloadInstallEventConverter) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME);
    downloadHadProgress = false;
  }

  @Override public void send() {
    if (downloadHadProgress) {
      super.send();
    } else {
      Logger.e(this,
          new IllegalArgumentException("The Result status should be added before send the event"));
    }
  }
}
