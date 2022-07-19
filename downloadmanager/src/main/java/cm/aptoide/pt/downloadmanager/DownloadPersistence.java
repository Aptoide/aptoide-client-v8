package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

public interface DownloadPersistence {

  Observable<List<RoomDownload>> getAll();

  Single<RoomDownload> getAsSingle(String md5);

  Observable<RoomDownload> getAsObservable(String md5);

  Completable delete(String md5);

  Completable save(RoomDownload download);

  Observable<List<RoomDownload>> getRunningDownloads();

  Observable<List<RoomDownload>> getInQueueSortedDownloads();

  Observable<List<RoomDownload>> getAsList(String md5);

  Observable<List<RoomDownload>> getUnmovedFilesDownloads();

  Completable delete(String packageName, int versionCode);

  Observable<List<RoomDownload>> getOutOfSpaceDownloads();
}