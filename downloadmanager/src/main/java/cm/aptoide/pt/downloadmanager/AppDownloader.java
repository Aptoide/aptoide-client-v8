package cm.aptoide.pt.downloadmanager;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface AppDownloader {

  void startAppDownload();

  Completable removeAppDownload();

  Observable<AppDownloadStatus> observeDownloadProgress();

  void stop();
}
