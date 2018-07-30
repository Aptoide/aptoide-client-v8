package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface AppDownloader {

  Observable<Download> startAppDownload(Download download);

  void pauseAppDownload(String md5);

  Observable<Download> getAppDownload(String md5);

  Observable<Download> getCurrentActiveDownload();

  void removeDownload(String md5);
}
