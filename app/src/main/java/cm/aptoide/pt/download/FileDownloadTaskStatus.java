package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus.AppDownloadState;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public class FileDownloadTaskStatus implements FileDownloadCallback {

  private AppDownloadState appDownloadState;
  private int downloadProgress;
  private int fileType;
  private String md5;

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, int downloadProgress,
      int fileType, String md5) {
    this.appDownloadState = appDownloadState;
    this.downloadProgress = downloadProgress;
    this.fileType = fileType;
    this.md5 = md5;
  }

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, String md5) {
    this.appDownloadState = appDownloadState;
    this.md5 = md5;
  }

  public String getMd5() {
    return md5;
  }

  @Override public int getDownloadProgress() {
    return downloadProgress;
  }

  @Override public int getFileType() {
    return fileType;
  }

  @Override public AppDownloadState getDownloadState() {
    return appDownloadState;
  }
}
