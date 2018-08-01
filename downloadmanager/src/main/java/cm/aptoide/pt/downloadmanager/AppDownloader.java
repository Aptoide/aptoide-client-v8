package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface AppDownloader {

  Completable startAppDownload();

  Completable pauseAppDownload();

  Observable<Download> getAppDownload(String md5);

  Observable<Download> getCurrentActiveDownload();

  void removeDownload(String md5);
}
