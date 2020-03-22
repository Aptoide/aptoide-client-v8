package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface DownloadPersistence {

  Observable<List<RoomDownload>> getAll();

  Observable<RoomDownload> get(String md5);

  Completable delete(String md5);

  Completable save(RoomDownload download);

  Observable<List<RoomDownload>> getRunningDownloads();

  Observable<List<RoomDownload>> getInQueueSortedDownloads();

  Observable<List<RoomDownload>> getAsList(String md5);

  Observable<List<RoomDownload>> getUnmovedFilesDownloads();
}