package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import java.util.HashMap;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class NewAptoideDownloadManager implements DownloadManager {

  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;

  public NewAptoideDownloadManager(DownloadsRepository downloadsRepository) {
    this.downloadsRepository = downloadsRepository;
    appDownloaderMap = new HashMap<>();
  }

  public void start() {
    dispatchDownloads();
  }

  @Override public void stop() {
  }

  @Override public Completable startDownload(Download download) {
    return Completable.fromAction(() -> {
      download.setOverallDownloadStatus(Download.IN_QUEUE);
      download.setTimeStamp(System.currentTimeMillis());
      downloadsRepository.save(download);
    })
        .subscribeOn(Schedulers.computation());
  }

  @Override public Observable<Download> getDownload(String md5) {
    return downloadsRepository.getDownload(md5);
  }

  @Override public Observable<Download> getDownloadsByMd5(String md5) {
    return null;
  }

  @Override public Observable<List<Download>> getDownloadsList() {
    return null;
  }

  @Override public Observable<Download> getCurrentActiveDownload() {
    return null;
  }

  @Override public Observable<List<Download>> getCurrentActiveDownloads() {
    return null;
  }

  @Override public Completable pauseAllDownloads() {
    return downloadsRepository.getDownloadsInProgress()
        .filter(downloads -> !downloads.isEmpty())
        .flatMapIterable(downloads -> downloads)
        .flatMap(download -> getAppDownloader(download.getMd5()).flatMapCompletable(
            appDownloader -> appDownloader.pauseAppDownload())
            .map(appDownloader -> download))
        .toCompletable();
  }

  @Override public Completable pauseDownload(String md5) {
    return downloadsRepository.getDownload(md5)
        .first()
        .map(download -> {
          download.setOverallDownloadStatus(Download.PAUSED);
          downloadsRepository.save(download);
          return download;
        })
        .flatMap(download -> getAppDownloader(download.getMd5()))
        .flatMapCompletable(appDownloader -> appDownloader.pauseAppDownload())
        .toCompletable();
  }

  @Override public Observable<Integer> getDownloadStatus(String md5) {
    return null;
  }

  @Override public Completable removeDownload(String md5) {
    return downloadsRepository.getDownload(md5)
        .first()
        .flatMap(download -> getAppDownloader(download.getMd5()).flatMapCompletable(
            appDownloader -> appDownloader.removeAppDownload()))
        .toCompletable();
  }

  private void dispatchDownloads() {
    downloadsRepository.getInQueueDownloads()
        .first()
        .filter(downloads -> !downloads.isEmpty())
        .map(downloads -> downloads.get(0))
        .flatMap(download -> getAppDownloader(download.getMd5()).flatMapCompletable(
            appDownloader -> appDownloader.startAppDownload()));
    // TODO: 8/27/18 dont forget to add the download to the db
  }

  private Observable<AppDownloader> getAppDownloader(String md5) {
    return Observable.just(appDownloaderMap.get(md5));
  }
}
