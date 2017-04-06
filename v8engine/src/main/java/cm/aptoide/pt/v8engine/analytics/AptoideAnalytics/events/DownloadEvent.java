package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
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
  private static final String TAG = DownloadEvent.class.getSimpleName();
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
      DownloadEventConverter downloadInstallEventConverter,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME, bodyInterceptor);
    downloadHadProgress = false;
  }

  @Override public void send() {
    super.send();
    Throwable error = getError();
    if (error != null) {
      CrashReport.getInstance().log(error);
      Logger.e(TAG, "send: " + error);
    }
  }

  @Override public boolean isReadyToSend() {
    return super.isReadyToSend() && downloadHadProgress;
  }
}
