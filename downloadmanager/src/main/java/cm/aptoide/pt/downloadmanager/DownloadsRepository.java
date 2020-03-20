package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.accessors.RoomDownloadPersistence;
import cm.aptoide.pt.database.realm.RoomDownload;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 8/21/18.
 */

public class DownloadsRepository {

  private RoomDownloadPersistence downloadPersistence;

  public DownloadsRepository(RoomDownloadPersistence downloadPersistence) {
    this.downloadPersistence = downloadPersistence;
  }

  public void save(RoomDownload download) {
    downloadPersistence.save(download);
  }

  public Observable<RoomDownload> getDownload(String md5) {
    return downloadPersistence.get(md5);
  }

  public Observable<List<RoomDownload>> getDownloadsInProgress() {
    return downloadPersistence.getRunningDownloads();
  }

  public Observable<List<RoomDownload>> getInQueueDownloads() {
    return downloadPersistence.getInQueueSortedDownloads();
  }

  public Observable<List<RoomDownload>> getAllDownloads() {
    return downloadPersistence.getAll();
  }

  public Observable<List<RoomDownload>> getWaitingToMoveFilesDownloads() {
    return downloadPersistence.getUnmovedFilesDownloads();
  }

  public Completable remove(String md5) {
    return Completable.fromAction(() -> downloadPersistence.delete(md5));
  }

  public Observable<List<RoomDownload>> getDownloadListByMd5(String md5) {
    return downloadPersistence.getAsList(md5);
  }

  public Observable<List<RoomDownload>> getCurrentActiveDownloads() {
    return downloadPersistence.getRunningDownloads();
  }

  public Observable<List<RoomDownload>> getInProgressDownloadsList() {
    return downloadPersistence.getRunningDownloads()
        .flatMap(downloads -> Observable.from(downloads)
            .filter(download -> download.getOverallDownloadStatus() == RoomDownload.PROGRESS
                || download.getOverallDownloadStatus() == (RoomDownload.PENDING))
            .toList());
  }
}
