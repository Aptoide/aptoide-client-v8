package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import java.util.HashMap;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Subscription;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AptoideDownloadManager implements DownloadManager {

  private static final String TAG = "AptoideDownloadManager";
  private final String cachePath;
  private final DownloadAppMapper downloadAppMapper;
  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;
  private DownloadStatusMapper downloadStatusMapper;
  private AppDownloaderProvider appDownloaderProvider;
  private Subscription dispatchDownloadsSubscription;
  private DownloadAnalytics downloadAnalytics;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper, String cachePath,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      DownloadAnalytics downloadAnalytics) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.cachePath = cachePath;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
    this.downloadAnalytics = downloadAnalytics;
    this.appDownloaderMap = new HashMap<>();
  }

  public synchronized void start() {
    dispatchDownloadsSubscription = downloadsRepository.getInProgressDownloadsList()
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
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
        .flatMap(download -> getAppDownloader(download.getMd5()).doOnError(
            throwable -> removeDownload(download.getMd5()))
            .doOnNext(AppDownloader::startAppDownload)
            .flatMap(this::handleDownloadProgress))
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  @Override public void stop() {
    if (!dispatchDownloadsSubscription.isUnsubscribed()) {
      dispatchDownloadsSubscription.unsubscribe();
    }
  }

  @Override public Completable startDownload(Download download) {
    return Completable.fromAction(() -> {
      download.setOverallDownloadStatus(Download.IN_QUEUE);
      download.setTimeStamp(System.currentTimeMillis());
      downloadsRepository.save(download);
      appDownloaderMap.put(download.getMd5(), createAppDownloadManager(download));
    });
  }

  @Override public Observable<Download> getDownload(String md5) {
    return downloadsRepository.getDownload(md5)
        .flatMap(download -> {
          if (download == null || isFileMissingFromCompletedDownload(download)) {
            return Observable.error(new DownloadNotFoundException());
          } else {
            return Observable.just(download);
          }
        })
        .takeUntil(
            storedDownload -> storedDownload.getOverallDownloadStatus() == Download.COMPLETED);
  }

  @Override public Observable<Download> getDownloadsByMd5(String md5) {
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

  @Override public Observable<List<Download>> getDownloadsList() {
    return downloadsRepository.getAllDownloads();
  }

  @Override public Observable<Download> getCurrentInProgressDownload() {
    return getDownloadsList().flatMapIterable(downloads -> downloads)
        .filter(download -> download.getOverallDownloadStatus() == Download.PROGRESS);
  }

  @Override public Observable<List<Download>> getCurrentActiveDownloads() {
    return downloadsRepository.getCurrentActiveDownloads();
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
    return getDownload(md5).map(download -> {
      if (download != null) {
        if (download.getOverallDownloadStatus() == Download.COMPLETED) {
          return getStateIfFileExists(download);
        }
        return download.getOverallDownloadStatus();
      } else {
        return Download.NOT_DOWNLOADED;
      }
    });
  }

  @Override public Completable removeDownload(String md5) {
    return downloadsRepository.getDownload(md5)
        .first()
        .flatMap(download -> getAppDownloader(download.getMd5()).flatMap(
            appDownloader -> appDownloader.removeAppDownload()
                .andThen(downloadsRepository.remove(md5))
                .andThen(Observable.just(download))))
        .doOnNext(download -> removeDownloadFiles(download))
        .toCompletable();
  }

  @Override public Completable invalidateDatabase() {
    return getDownloadsList().first()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> getStateIfFileExists(download) == Download.FILE_MISSING)
        .flatMapCompletable(download -> downloadsRepository.remove(download.getMd5()))
        .toList()
        .toCompletable();
  }

  private void removeDownloadFiles(Download download) {
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      FileUtils.removeFile(fileToDownload.getFilePath());
      FileUtils.removeFile(cachePath + fileToDownload.getFileName() + ".temp");
    }
  }

  private AppDownloader createAppDownloadManager(Download download) {
    DownloadApp downloadApp = downloadAppMapper.mapDownload(download);
    return appDownloaderProvider.getAppDownloader(downloadApp);
  }

  private boolean isFileMissingFromCompletedDownload(Download download) {
    return download.getOverallDownloadStatus() == Download.COMPLETED
        && getStateIfFileExists(download) == Download.FILE_MISSING;
  }

  private int getStateIfFileExists(Download download) {
    int downloadState = Download.COMPLETED;
    if (download.getOverallDownloadStatus() == Download.PROGRESS) {
      downloadState = Download.PROGRESS;
    } else {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
          downloadState = Download.FILE_MISSING;
          Logger.getInstance()
              .d(TAG, "File is missing: "
                  + fileToDownload.getFileName()
                  + " file path: "
                  + fileToDownload.getFilePath());
          break;
        }
      }
    }
    return downloadState;
  }

  private Observable<Download> handleDownloadProgress(AppDownloader appDownloader) {
    return appDownloader.observeDownloadProgress()
        .flatMap(appDownloadStatus -> downloadsRepository.getDownload(appDownloadStatus.getMd5())
            .first()
            .flatMap(download -> updateDownload(download, appDownloadStatus)))
        .filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .doOnNext(download -> removeAppDownloader(download.getMd5()))
        .doOnNext(download -> downloadAnalytics.onDownloadComplete(download.getMd5(),
            download.getPackageName(), download.getVersionCode()))
        .takeUntil(download -> download.getOverallDownloadStatus() == Download.COMPLETED);
  }

  private void removeAppDownloader(String md5) {
    AppDownloader appDownloader = appDownloaderMap.get(md5);
    Logger.getInstance()
        .d(TAG, "removing download manager");
    if (appDownloader != null) {
      appDownloader.stop();
      Logger.getInstance()
          .d(TAG, "removed download manager ");
      appDownloaderMap.remove(md5);
    }
  }

  private Observable<Download> updateDownload(Download download,
      AppDownloadStatus appDownloadStatus) {
    if(appDownloadStatus.getDownloadStatus().equals(AppDownloadStatus.AppDownloadState.ERROR_MD5_DOES_NOT_MATCH)){
      removeDownloadFiles(download);
    }
    download.setOverallProgress(appDownloadStatus.getOverallProgress());
    download.setOverallDownloadStatus(
        downloadStatusMapper.mapAppDownloadStatus(appDownloadStatus.getDownloadStatus()));
    download.setDownloadError(
        downloadStatusMapper.mapDownloadError(appDownloadStatus.getDownloadStatus()));
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      fileToDownload.setStatus(downloadStatusMapper.mapAppDownloadStatus(
          appDownloadStatus.getFileDownloadStatus(fileToDownload.getMd5())));
      fileToDownload.setProgress(
          appDownloadStatus.getFileDownloadProgress(fileToDownload.getMd5()));
    }
    downloadsRepository.save(download);
    return Observable.just(download);
  }

  private Observable<AppDownloader> getAppDownloader(String md5) {
    return Observable.just(appDownloaderMap.get(md5));
  }
}
