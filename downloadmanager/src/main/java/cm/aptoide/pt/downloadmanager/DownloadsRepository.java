package cm.aptoide.pt.downloadmanager;

import androidx.room.EmptyResultSetException;
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

/**
 * Created by filipegoncalves on 8/21/18.
 */

public class DownloadsRepository {

  private final DownloadPersistence downloadPersistence;

  public DownloadsRepository(DownloadPersistence downloadPersistence) {
    this.downloadPersistence = downloadPersistence;
  }

  public Completable save(DownloadEntity download) {
    return downloadPersistence.save(download);
  }

  public Completable remove(String md5) {
    return downloadPersistence.delete(md5);
  }

  public Single<DownloadEntity> getDownloadAsSingle(String md5) {
    return downloadPersistence.getAsSingle(md5)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof EmptyResultSetException) {
            return Single.error(new DownloadNotFoundException());
          }
          return Single.error(throwable);
        })
        .doOnError(throwable -> {
          throwable.printStackTrace();
        });
  }

  public Observable<DownloadEntity> getDownloadAsObservable(String md5) {
    return downloadPersistence.getAsObservable(md5);
  }

  public Observable<List<DownloadEntity>> getDownloadsInProgress() {
    return downloadPersistence.getRunningDownloads();
  }

  public Observable<List<DownloadEntity>> getInQueueDownloads() {
    return downloadPersistence.getInQueueSortedDownloads();
  }

  public Observable<List<DownloadEntity>> getAllDownloads() {
    return downloadPersistence.getAll();
  }

  public Observable<List<DownloadEntity>> getWaitingToMoveFilesDownloads() {
    return downloadPersistence.getUnmovedFilesDownloads();
  }

  public Observable<List<DownloadEntity>> getCurrentActiveDownloads() {
    return downloadPersistence.getRunningDownloads();
  }

  public Observable<List<DownloadEntity>> getInProgressDownloadsList() {
    return downloadPersistence.getRunningDownloads()
        .flatMapSingle(downloads -> Observable.fromIterable(downloads)
            .filter(download -> download.getOverallDownloadStatus() == DownloadEntity.PROGRESS
                || download.getOverallDownloadStatus() == (DownloadEntity.PENDING))
            .toList());
  }

  public Observable<List<DownloadEntity>> getOutOfSpaceDownloads() {
    return downloadPersistence.getOutOfSpaceDownloads();
  }

  public Observable<DownloadEntity> getCompletedDownload(String packageName) {
    return downloadPersistence.getCompletedDownload(packageName);
  }
}
