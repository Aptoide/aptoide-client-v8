package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus.AppDownloadState;
import cm.aptoide.pt.downloadmanager.DownloadError;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import cm.aptoide.pt.downloadmanager.FileDownloadProgressResult;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public class FileDownloadTaskStatus implements FileDownloadCallback {

  private AppDownloadState appDownloadState;
  private FileDownloadProgressResult downloadProgress;
  private String md5;
  private DownloadError error;
  private int averageDownloadSpeed;
  private int fileType;

  public FileDownloadTaskStatus(AppDownloadState appDownloadState,
      FileDownloadProgressResult downloadProgress, String md5, int averageDownloadSpeed,
      int fileType) {
    this.appDownloadState = appDownloadState;
    this.downloadProgress = downloadProgress;
    this.md5 = md5;
    this.averageDownloadSpeed = averageDownloadSpeed;
    this.fileType = fileType;
    this.error = null;
  }

  public FileDownloadTaskStatus(AppDownloadState appDownloadState, String md5,
      DownloadError error, int averageDownloadSpeed, int fileType) {
    this.appDownloadState = appDownloadState;
    this.md5 = md5;
    this.error = error;
    this.averageDownloadSpeed = averageDownloadSpeed;
    this.fileType = fileType;
    this.downloadProgress = new FileDownloadProgressResult(0, 0);
  }

  @Override public FileDownloadProgressResult getDownloadProgress() {
    return downloadProgress;
  }

  @Override public AppDownloadState getDownloadState() {
    return appDownloadState;
  }

  @Override public String getMd5() {
    return md5;
  }

  @Override public boolean hasError() {
    return error != null;
  }

  @Override public DownloadError getError() {
    return error;
  }

  @Override public int getDownloadSpeed() {
    return averageDownloadSpeed;
  }

  @Override public int getFileType() {
    return fileType;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final FileDownloadTaskStatus that = (FileDownloadTaskStatus) o;

    return md5.equals(that.getMd5());
  }
}
