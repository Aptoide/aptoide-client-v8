package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public interface FileDownloadCallback {
  FileDownloadProgressResult getDownloadProgress();

  AppDownloadStatus.AppDownloadState getDownloadState();

  String getMd5();

  boolean hasError();

  Throwable getError();
}
