package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by trinkes on 02/01/2017.
 */

public @EqualsAndHashCode(callSuper = false) @Data @ToString class DownloadEvent
    extends DownloadInstallBaseEvent {
  private static final String EVENT_NAME = "DOWNLOAD";
  /**
   * this variable should be activated when the download progress starts, this will prevent the
   * event to be sent if download was cached
   */
  @Setter private boolean downloadHadProgress;
  @Setter @Getter private String mirrorApk;
  @Setter @Getter private String mirrorObbMain;
  @Setter @Getter private String mirrorObbPatch;

  public DownloadEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadEventConverter downloadInstallEventConverter) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME);
    downloadHadProgress = false;
  }

  @Override public boolean isReadyToSend() {
    return super.isReadyToSend() && downloadHadProgress;
  }
}
