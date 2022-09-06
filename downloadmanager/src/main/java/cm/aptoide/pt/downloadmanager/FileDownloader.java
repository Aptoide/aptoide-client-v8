package cm.aptoide.pt.downloadmanager;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public interface FileDownloader {
  Completable startFileDownload();

  Completable removeDownloadFile();

  Observable<FileDownloadCallback> observeFileDownloadProgress();

  void stopFailedDownload();
}
