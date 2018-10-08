package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus.AppDownloadState;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public class FileDownloadTaskStatus implements FileDownloadCallback {

  private AppDownloadState appDownloadState;
  private int downloadProgress;
  private String md5;

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, int downloadProgress,
      String md5) {
    this.appDownloadState = appDownloadState;
    this.downloadProgress = downloadProgress;
    this.md5 = md5;
  }

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, String md5) {
    this.appDownloadState = appDownloadState;
    this.md5 = md5;
    this.downloadProgress = 0;
  }

  @Override public int getDownloadProgress() {
    return downloadProgress;
  }

  @Override public AppDownloadState getDownloadState() {
    return appDownloadState;
  }

  @Override public String getMd5() {
    return md5;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final FileDownloadTaskStatus that = (FileDownloadTaskStatus) o;

    return md5.equals(that.getMd5());
  }
}
