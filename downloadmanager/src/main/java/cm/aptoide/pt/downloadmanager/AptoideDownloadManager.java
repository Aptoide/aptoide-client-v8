package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomFileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.Subscription;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AptoideDownloadManager implements DownloadManager {

  private static final String TAG = "AptoideDownloadManager";
  private final DownloadAppMapper downloadAppMapper;
  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;
  private DownloadStatusMapper downloadStatusMapper;
  private AppDownloaderProvider appDownloaderProvider;
  private Subscription dispatchDownloadsSubscription;
  private DownloadAnalytics downloadAnalytics;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      DownloadAnalytics downloadAnalytics) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
    this.downloadAnalytics = downloadAnalytics;
    this.appDownloaderMap = new HashMap<>();
  }

  public synchronized void start() {
    dispatchDownloads();
  }

  @Override public void stop() {
    if (!dispatchDownloadsSubscription.isUnsubscribed()) {
      dispatchDownloadsSubscription.unsubscribe();
    }
  }

  @Override public Completable startDownload(RoomDownload download) {
    return Completable.fromAction(() -> {
          download.setOverallDownloadStatus(RoomDownload.IN_QUEUE);
          download.setTimeStamp(System.currentTimeMillis());
        })
        .andThen(downloadsRepository.save(download))
        .doOnCompleted(
            () -> appDownloaderMap.put(download.getMd5(), createAppDownloadManager(download)));
  }

  @Override public Observable<RoomDownload> getDownloadAsObservable(String md5) {
    return downloadsRepository.getDownloadAsObservable(md5)
        .flatMap(download -> {
          if (download == null || isFileMissingFromCompletedDownload(download)) {
            return Observable.error(new DownloadNotFoundException());
          } else {
            return Observable.just(download);
          }
        })
        .takeUntil(
            storedDownload -> storedDownload.getOverallDownloadStatus() == RoomDownload.COMPLETED);
  }

  @Override public Single<RoomDownload> getDownloadAsSingle(String md5) {
    return downloadsRepository.getDownloadAsSingle(md5)
        .flatMap(download -> {
          if (download == null || isFileMissingFromCompletedDownload(download)) {
            return Single.error(new DownloadNotFoundException());
          } else {
            return Single.just(download);
          }
        });
  }

  @Override public Observable<RoomDownload> getDownloadsByMd5(String md5) {
    return downloadsRepository.getDownloadListByMd5(md5)
        .flatMap(downloads -> Observable.from(downloads)
            .filter(download -> download != null && !isFileMissingFromCompletedDownload(download))
            .toList())
        .map(downloads -> {
          if (downloads.isEmpty()) {
            return null;
          } else {
            return downloads.get(0);
          }
        })
        .distinctUntilChanged()
        .doOnNext(download -> Logger.getInstance()
            .d(TAG, "passing a download : "));
  }

  @Override public Observable<List<RoomDownload>> getDownloadsList() {
    return downloadsRepository.getAllDownloads();
  }

  @Override public Observable<RoomDownload> getCurrentInProgressDownload() {
    return getDownloadsList().flatMapIterable(downloads -> downloads)
        .filter(download -> download.getOverallDownloadStatus() == RoomDownload.PROGRESS);
  }

  @Override public Observable<List<RoomDownload>> getCurrentActiveDownloads() {
    return downloadsRepository.getCurrentActiveDownloads();
  }

  @Override public Completable pauseAllDownloads() {
    return downloadsRepository.getDownloadsInProgress()
        .filter(downloads -> !downloads.isEmpty())
        .flatMapIterable(downloads -> downloads)
        .flatMap(download -> getAppDownloader(download).flatMapCompletable(
                appDownloader -> appDownloader.pauseAppDownload())
            .map(appDownloader -> download))
        .toCompletable();
  }

  @Override public Completable pauseDownload(String md5) {
    return downloadsRepository.getDownloadAsObservable(md5)
        .first()
        .doOnError(throwable -> throwable.printStackTrace())
        .flatMap(download -> {
          download.setOverallDownloadStatus(RoomDownload.PAUSED);
          return downloadsRepository.save(download)
              .andThen(Observable.just(download));
        })
        .flatMap(download -> getAppDownloader(download))
        .flatMapCompletable(appDownloader -> appDownloader.pauseAppDownload())
        .toCompletable();
  }

  @Override public Completable removeDownload(String md5) {
    return downloadsRepository.getDownloadAsObservable(md5)
        .first()
        .flatMap(download -> getAppDownloader(download).flatMap(
            appDownloader -> appDownloader.removeAppDownload()
                .andThen(downloadsRepository.remove(md5))
                .andThen(Observable.just(download))))
        .doOnNext(download -> removeDownloadFiles(download))
        .toCompletable();
  }

  @Override public Completable invalidateDatabase() {
    return getDownloadsList().first()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> getStateIfFileExists(download) == RoomDownload.FILE_MISSING)
        .flatMapCompletable(download -> downloadsRepository.remove(download.getMd5()))
        .toList()
        .toCompletable();
  }

  private void dispatchDownloads() {
    dispatchDownloadsSubscription = downloadsRepository.getInProgressDownloadsList()
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
        .throttleLast(750, TimeUnit.MILLISECONDS)
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Downloads in Progress " + downloads.size()))
        .filter(List::isEmpty)
        .flatMap(__ -> downloadsRepository.getInQueueDownloads()
            .first())
        .distinctUntilChanged()
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Queued downloads " + downloads.size()))
        .filter(downloads -> !downloads.isEmpty())
        .map(downloads -> downloads.get(0))
        .flatMap(download -> getAppDownloader(download).doOnError(
                throwable -> removeDownloadFiles(download))
            .doOnNext(AppDownloader::startAppDownload)
            .flatMap(this::handleDownloadProgress))
        .retry()
        .doOnError(throwable -> throwable.printStackTrace())
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  private void removeDownloadFiles(RoomDownload download) {
    for (final RoomFileToDownload fileToDownload : download.getFilesToDownload()) {
      FileUtils.removeFile(fileToDownload.getFilePath());
    }
  }

  private AppDownloader createAppDownloadManager(RoomDownload download) {
    DownloadApp downloadApp = downloadAppMapper.mapDownload(download);
    return appDownloaderProvider.getAppDownloader(downloadApp);
  }

  private boolean isFileMissingFromCompletedDownload(RoomDownload download) {
    return download.getOverallDownloadStatus() == RoomDownload.COMPLETED
        && getStateIfFileExists(download) == RoomDownload.FILE_MISSING;
  }

  private int getStateIfFileExists(RoomDownload download) {
    int downloadState = RoomDownload.COMPLETED;
    if (download.getOverallDownloadStatus() == RoomDownload.PROGRESS) {
      downloadState = RoomDownload.PROGRESS;
    } else {
      for (final RoomFileToDownload roomFileToDownload : download.getFilesToDownload()) {
        if (!FileUtils.fileExists(roomFileToDownload.getFilePath())) {
          downloadState = RoomDownload.FILE_MISSING;
          Logger.getInstance()
              .d(TAG, "File is missing: "
                  + roomFileToDownload.getFileName()
                  + " file path: "
                  + roomFileToDownload.getFilePath());
          break;
        }
      }
    }
    return downloadState;
  }

  private Observable<RoomDownload> handleDownloadProgress(AppDownloader appDownloader) {
    return appDownloader.observeDownloadProgress()
        .flatMap(appDownloadStatus -> downloadsRepository.getDownloadAsObservable(
                appDownloadStatus.getMd5())
            .first()
            .flatMap(download -> updateDownload(download, appDownloadStatus).andThen(
                Observable.just(download))))
        .doOnNext(download -> {
          if (download.getOverallDownloadStatus() == RoomDownload.PROGRESS) {
            downloadAnalytics.startProgress(download);
          }
        })
        .filter(
            download -> download.getOverallDownloadStatus() == RoomDownload.COMPLETED)
        .doOnNext(download -> downloadAnalytics.onDownloadComplete(download.getMd5(),
            download.getPackageName(), download.getVersionCode()))
        .doOnNext(download -> removeAppDownloader(download.getMd5()))
        .takeUntil(
            download -> download.getOverallDownloadStatus() == RoomDownload.COMPLETED);
  }

  private void removeAppDownloader(String md5) {
    AppDownloader appDownloader = appDownloaderMap.get(md5);
    Logger.getInstance()
        .d(TAG, "removing download manager from app : " + md5);
    if (appDownloader != null) {
      appDownloader.stop();
      Logger.getInstance()
          .d(TAG, "removed download manager from app " + md5);
      appDownloaderMap.remove(md5);
    }
  }

  private Completable updateDownload(RoomDownload download, AppDownloadStatus appDownloadStatus) {
    download.setOverallProgress(appDownloadStatus.getOverallProgress());
    download.setOverallDownloadStatus(
        downloadStatusMapper.mapAppDownloadStatus(appDownloadStatus.getDownloadStatus()));
    download.setDownloadError(
        downloadStatusMapper.mapDownloadError(appDownloadStatus.getDownloadStatus()));
    for (final RoomFileToDownload roomFileToDownload : download.getFilesToDownload()) {
      roomFileToDownload.setStatus(downloadStatusMapper.mapAppDownloadStatus(
          appDownloadStatus.getFileDownloadStatus(roomFileToDownload.getMd5())));
      roomFileToDownload.setProgress(
          appDownloadStatus.getFileDownloadProgress(roomFileToDownload.getMd5()));
    }
    if (appDownloadStatus.getDownloadStatus()
        .equals(AppDownloadStatus.AppDownloadState.ERROR_MD5_DOES_NOT_MATCH)) {
      removeDownloadFiles(download);
    }
    return downloadsRepository.save(download);
  }

  private Observable<AppDownloader> getAppDownloader(RoomDownload download) {
    return Observable.just(appDownloaderMap.get(download.getMd5()))
        .map(appDownloader -> {
          if (appDownloader == null) {
            // TODO: 2019-11-12 This is a work around to fix the problem
            //  related with appdownloader being null.
            //  We are going to investigate the source of this problem
            //  on https://aptoide.atlassian.net/browse/ASV-2085
            return createAppDownloadManager(download);
          }
          return appDownloader;
        });
  }
}
