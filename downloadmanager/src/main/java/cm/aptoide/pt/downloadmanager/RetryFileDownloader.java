package cm.aptoide.pt.downloadmanager;

import rx.Completable;
import rx.Observable;

public interface RetryFileDownloader {

  void startFileDownload();

  Completable pauseDownload();

  Completable removeDownloadFile();

  Observable<FileDownloadCallback> observeFileDownloadProgress();

  void stop();

  void stopFailedDownload();
}
