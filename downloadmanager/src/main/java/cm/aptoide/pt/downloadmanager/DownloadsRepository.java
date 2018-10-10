package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 8/21/18.
 */

public class DownloadsRepository {

  private DownloadAccessor downloadAccessor;

  public DownloadsRepository(DownloadAccessor downloadAccessor) {
    this.downloadAccessor = downloadAccessor;
  }

  public void save(Download download) {
    downloadAccessor.save(download);
  }

  public Observable<Download> getDownload(String md5) {
    return downloadAccessor.get(md5);
  }

  public Observable<List<Download>> getDownloadsInProgress() {
    return downloadAccessor.getRunningDownloads();
  }

  public Observable<List<Download>> getInQueueDownloads() {
    return downloadAccessor.getInQueueSortedDownloads();
  }

  public Observable<List<Download>> getAllDownloads() {
    return downloadAccessor.getAll();
  }

  public Completable remove(String md5) {
    return Completable.fromAction(() -> downloadAccessor.delete(md5));
  }

  public Observable<List<Download>> getDownloadListByMd5(String md5) {
    return downloadAccessor.getAsList(md5);
  }

  public Observable<List<Download>> getCurrentActiveDownloads() {
    return downloadAccessor.getRunningDownloads();
  }

  public Observable<List<Download>> getInProgressDownloadsList() {
    return downloadAccessor.getRunningDownloads()
        .flatMap(downloads -> Observable.from(downloads)
            .filter(download -> download.getOverallDownloadStatus() == Download.PROGRESS)
            .toList())
        .distinctUntilChanged();
  }
}
