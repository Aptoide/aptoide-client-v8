package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.RoomDownload;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface DownloadManager {

  void start();

  void stop();

  Completable startDownload(RoomDownload download);

  Observable<RoomDownload> getDownload(String md5);

  Observable<RoomDownload> getDownloadsByMd5(String md5);

  Observable<List<RoomDownload>> getDownloadsList();

  Observable<RoomDownload> getCurrentInProgressDownload();

  Observable<List<RoomDownload>> getCurrentActiveDownloads();

  Completable pauseAllDownloads();

  Completable pauseDownload(String md5);

  Observable<Integer> getDownloadStatus(String md5);

  Completable removeDownload(String md5);

  Completable invalidateDatabase();
}
