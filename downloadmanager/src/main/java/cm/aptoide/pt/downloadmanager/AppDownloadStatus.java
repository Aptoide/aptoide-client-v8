package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 8/28/18.
 */

public class AppDownloadStatus {

  private String md5;
  private FileDownloadCallback apk;
  private FileDownloadCallback obbMain;
  private FileDownloadCallback obbPatch;
  private AppDownloadState appDownloadState;

  public AppDownloadStatus(String md5, FileDownloadCallback apk, FileDownloadCallback obbMain,
      FileDownloadCallback obbPatch, AppDownloadState appDownloadState) {
    this.md5 = md5;
    this.apk = apk;
    this.obbMain = obbMain;
    this.obbPatch = obbPatch;
    this.appDownloadState = appDownloadState;
  }

  public String getMd5() {
    return md5;
  }

  public int getOverallProgress() {
    int overallProgress = 0;
    if (apk != null) {
      overallProgress += apk.getDownloadProgress();
    }
    if (obbMain != null) {
      overallProgress += obbMain.getDownloadProgress();
    }
    if (obbPatch != null) {
      overallProgress += obbPatch.getDownloadProgress();
    }
    return overallProgress;
  }

  public AppDownloadState getDownloadStatus() {
    return appDownloadState;
  }

  public void setApk(FileDownloadCallback apk) {
    this.apk = apk;
  }

  public void setObbMain(FileDownloadCallback obbMain) {
    this.obbMain = obbMain;
  }

  public void setObbPatch(FileDownloadCallback obbPatch) {
    this.obbPatch = obbPatch;
  }

  public void setAppDownloadState(AppDownloadState appDownloadState) {
    this.appDownloadState = appDownloadState;
  }

  public enum AppDownloadState {
    INVALID_STATUS, COMPLETED, PENDING, PAUSED, WARN, ERROR, PROGRESS
  }
}
