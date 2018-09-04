package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 8/28/18.
 */

public class AppDownloadStatus {

  private String md5;
  private FileDownloadCallback apk;
  private FileDownloadCallback obbMain;
  private FileDownloadCallback obbPath;
  private int downloadSpeed;
  private AppDownloadState appDownloadState;

  public String getMd5() {
    return md5;
  }

  public int getOverallProgress() {
    return apk.getDownloadProgress()
        + obbMain.getDownloadProgress()
        + obbPath.getDownloadProgress();
  }

  public AppDownloadState getDownloadStatus() {
    return appDownloadState;
  }

  public int getDownloadSpeed() {
    return downloadSpeed;
  }

  enum AppDownloadState {
    INVALID_STATUS, COMPLETED, PENDING, PAUSED, WARN, ERROR
  }
}
