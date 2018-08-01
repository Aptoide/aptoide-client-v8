package cm.aptoide.pt.downloadmanager;

import rx.Completable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class FileDownloadManager implements FileDownloader {

  @Override public Completable startFileDownload(DownloadAppFile downloadAppFile) {
    return null;
  }

  @Override public Completable pauseDownload(DownloadAppFile downloadAppFile) {
    return null;
  }

  @Override public Completable removeDownloadFile(DownloadAppFile downloadAppFile) {
    return null;
  }
}
