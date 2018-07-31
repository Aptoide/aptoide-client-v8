package cm.aptoide.pt.downloadmanager;

import rx.Completable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class FileDownloadManager implements FileDownloader {
  @Override public Completable startFileDownload(DownloadAppFile downloadAppFile) {
    return null;
  }

  @Override public void pauseDownload() {

  }

  @Override public void pauseAllDownloads() {

  }
}
