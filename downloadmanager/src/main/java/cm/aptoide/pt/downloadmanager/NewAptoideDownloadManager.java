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
  private DownloadService downloadService;

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

  @Override public void pauseAllDownloads() {

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

  @Override public void removeDownload(String md5) {

  }

  private void dispatchDownloads() {
    /*
     downloadsRepository.getDownloads()
     .flatMapIterable(downloadsList -> downloadsList)
     .filter(download -> download.getOverallDownloadStatus() == PENDING)
     .toList()
     .filter(downloads -> !downloads.isEmpty())
     .map(downloads -> downloads.get(0))
     .flatMap(download -> getAppDownloader(download.getMd5()).flatMap(
     appDownloader -> downloadService.download(download, appDownloader)))
     .flatMapCompletable(download -> downloadsRepository.save(download))
     .subscribe(__ -> {
     }, throwable -> {
     throw new OnErrorNotImplementedException();
     });
     **/
  }

  private Observable<AppDownloader> getAppDownloader(String md5) {
    return Observable.just(appDownloaderMap.get(md5));
  }
}
