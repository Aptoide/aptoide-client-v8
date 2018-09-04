package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
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
  private int PROGRESS_MAX_VALUE = 100;
  private DownloadStatusMapper downloadStatusMapper;

  public NewAptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
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
            appDownloader -> appDownloader.startAppDownload()))
        .flatMap(appDownloader -> handleDownloadProgress(appDownloader));
  }

  private Observable<Download> handleDownloadProgress(AppDownloader appDownloader) {
    return appDownloader.observeDownloadProgress()
        .flatMap(appDownloadStatus -> downloadsRepository.getDownload(appDownloadStatus.getMd5())
            .first()
            .flatMap(download -> updateDownload(download, appDownloadStatus)))
        .doOnNext(download -> downloadsRepository.save(download));
  }

  private Observable<Download> updateDownload(Download download,
      AppDownloadStatus appDownloadStatus) {
    download.setOverallDownloadStatus(
        downloadStatusMapper.mapAppDownloadStatus(appDownloadStatus.getDownloadStatus()));
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      fileToDownload.setStatus(
          downloadStatusMapper.mapAppDownloadStatus(appDownloadStatus.getDownloadStatus()));
      fileToDownload.setProgress(appDownloadStatus.getOverallProgress());
    }
    download.setDownloadSpeed(appDownloadStatus.getDownloadSpeed());
    return Observable.just(download);
  }

  private Observable<AppDownloader> getAppDownloader(String md5) {
    return Observable.just(appDownloaderMap.get(md5));
  }
}
