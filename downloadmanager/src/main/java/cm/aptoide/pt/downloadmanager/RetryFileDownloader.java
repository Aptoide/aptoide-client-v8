package cm.aptoide.pt.downloadmanager;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface RetryFileDownloader {

  void startFileDownload();


  Completable removeDownloadFile();

  Observable<FileDownloadCallback> observeFileDownloadProgress();

  void stop();

  void stopFailedDownload();
}
