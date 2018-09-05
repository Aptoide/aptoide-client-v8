package cm.aptoide.pt.downloadmanager;

import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface AppDownloader {

  Completable startAppDownload();

  Completable pauseAppDownload();

  Completable removeAppDownload();

  Observable<AppDownloadStatus> observeDownloadProgress();
}
