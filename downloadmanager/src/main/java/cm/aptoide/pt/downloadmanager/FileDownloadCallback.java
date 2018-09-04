package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public interface FileDownloadCallback {
  int getDownloadProgress();

  int getType();

  AppDownloadStatus.AppDownloadState getDownloadState();
}
