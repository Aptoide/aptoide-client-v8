package cm.aptoide.pt.download;

import android.content.SharedPreferences;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 02/01/2017.
 */

public class DownloadEvent extends DownloadInstallBaseEvent {
  private static final String TAG = DownloadEvent.class.getSimpleName();
  private static final String EVENT_NAME = "DOWNLOAD";
  /**
   * this variable should be activated when the download progress starts, this will prevent the
   * event to be sent if download was cached
   */
  private boolean downloadHadProgress;
  private String mirrorApk;
  private String mirrorObbMain;
  private String mirrorObbPatch;

  public DownloadEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadEventConverter downloadInstallEventConverter,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, String previousContext, String screenHistoryStore,
      String screenHistoryTag) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, previousContext, screenHistoryStore, screenHistoryTag);
    downloadHadProgress = false;
  }

  public String getMirrorApk() {
    return mirrorApk;
  }

  public void setMirrorApk(String mirrorApk) {
    this.mirrorApk = mirrorApk;
  }

  public String getMirrorObbMain() {
    return mirrorObbMain;
  }

  public void setMirrorObbMain(String mirrorObbMain) {
    this.mirrorObbMain = mirrorObbMain;
  }

  public String getMirrorObbPatch() {
    return mirrorObbPatch;
  }

  public void setMirrorObbPatch(String mirrorObbPatch) {
    this.mirrorObbPatch = mirrorObbPatch;
  }

  public void setDownloadHadProgress(boolean downloadHadProgress) {
    this.downloadHadProgress = downloadHadProgress;
  }

  @Override public void send() {
    super.send();
    Throwable error = getError();
    if (error != null) {
      CrashReport.getInstance()
          .log(error);
      Logger.e(TAG, "send: " + error);
    }
  }

  @Override public boolean isReadyToSend() {
    return super.isReadyToSend() && downloadHadProgress;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DownloadEvent that = (DownloadEvent) o;

    if (downloadHadProgress != that.downloadHadProgress) return false;
    if (getMirrorApk() != null ? !getMirrorApk().equals(that.getMirrorApk())
        : that.getMirrorApk() != null) {
      return false;
    }
    if (getMirrorObbMain() != null ? !getMirrorObbMain().equals(that.getMirrorObbMain())
        : that.getMirrorObbMain() != null) {
      return false;
    }
    return getMirrorObbPatch() != null ? getMirrorObbPatch().equals(that.getMirrorObbPatch())
        : that.getMirrorObbPatch() == null;
  }

  @Override public int hashCode() {
    int result = (downloadHadProgress ? 1 : 0);
    result = 31 * result + (getMirrorApk() != null ? getMirrorApk().hashCode() : 0);
    result = 31 * result + (getMirrorObbMain() != null ? getMirrorObbMain().hashCode() : 0);
    result = 31 * result + (getMirrorObbPatch() != null ? getMirrorObbPatch().hashCode() : 0);
    return result;
  }
}
