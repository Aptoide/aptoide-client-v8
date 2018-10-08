package cm.aptoide.pt.downloadmanager;

import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public interface FileDownloader {
  Completable startFileDownload();

  Completable pauseDownload();

  Completable removeDownloadFile();

  Observable<FileDownloadCallback> observeFileDownloadProgress();

  void stopFailedDownload();
}
