package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

public interface DownloadPersistence {

  Observable<List<DownloadEntity>> getAll();

  Single<DownloadEntity> getAsSingle(String md5);

  Observable<DownloadEntity> getAsObservable(String md5);

  Completable delete(String md5);

  Completable save(DownloadEntity download);

  Observable<List<DownloadEntity>> getRunningDownloads();

  Observable<List<DownloadEntity>> getInQueueSortedDownloads();

  Observable<List<DownloadEntity>> getAsList(String md5);

  Observable<List<DownloadEntity>> getUnmovedFilesDownloads();

  Observable<List<DownloadEntity>> getOutOfSpaceDownloads();

  Observable<DownloadEntity> getCompletedDownload(String packageName);
}