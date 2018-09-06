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

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, int downloadProgress,
      int fileType) {
    this.appDownloadState = appDownloadState;
    this.downloadProgress = downloadProgress;
    this.fileType = fileType;
  }

  public FileDownloadTaskStatus(AppDownloadState appDownloadState) {
    this.appDownloadState = appDownloadState;
  }

  @Override public int getDownloadProgress() {
    return downloadProgress;
  }

  @Override public int getType() {
    return fileType;
  }

  @Override public AppDownloadState getDownloadState() {
    return appDownloadState;
  }
}
