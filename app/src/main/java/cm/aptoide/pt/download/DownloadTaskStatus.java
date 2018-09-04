package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus.AppDownloadState;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public class DownloadTaskStatus implements FileDownloadCallback {

  private AppDownloadState appDownloadState;
  private int downloadProgress;
  private int fileType;

  public DownloadTaskStatus(AppDownloadState state, int downloadProgress, int fileType) {
    this.appDownloadState = state;
    this.downloadProgress = downloadProgress;
    this.fileType = fileType;
  }

  public DownloadTaskStatus(AppDownloadState appDownloadState) {
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
