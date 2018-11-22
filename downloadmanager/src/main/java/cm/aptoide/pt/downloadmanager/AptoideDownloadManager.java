package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
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
  private final String apkPath;
  private final String obbPath;
  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;
  private DownloadStatusMapper downloadStatusMapper;
  private AppDownloaderProvider appDownloaderProvider;
  private Subscription dispatchDownloadsSubscription;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper, String cachePath,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      String apkPath, String obbPath) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.cachePath = cachePath;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
    appDownloaderMap = new HashMap<>();
  }

  public synchronized void start() {
    dispatchDownloadsSubscription = downloadsRepository.getInProgressDownloadsList()
        .doOnError(Throwable::printStackTrace)
        .retry()
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Downloads in Progress " + downloads.size()))
        .filter(List::isEmpty)
        .flatMap(__ -> downloadsRepository.getInQueueDownloads()
            .first())
        .distinctUntilChanged()
        .doOnError(Throwable::printStackTrace)
        .retry()
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Queued downloads " + downloads.size()))
        .filter(downloads -> !downloads.isEmpty())
        .map(downloads -> downloads.get(0))
        .flatMap(download -> getAppDownloader(download).doOnNext(
            appDownloader -> handleStartDownload(appDownloader, download))
            .flatMap(appDownloader -> handleDownloadProgress(appDownloader,
                download.getOverallDownloadStatus(), download.getMd5())))
        .doOnError(Throwable::printStackTrace)
        .retry()
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  @Override public void stop() {
    if (dispatchDownloadsSubscription != null && !dispatchDownloadsSubscription.isUnsubscribed()) {
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
        .distinctUntilChanged();
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
        .flatMap(download -> getAppDownloader(download).flatMapCompletable(
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
        .flatMap(download -> getAppDownloader(download))
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
          break;
        }
      }
    }
    return downloadState;
  }

  private Observable<Download> handleDownloadProgress(AppDownloader appDownloader,
      int overallDownloadStatus, String md5) {
    if (overallDownloadStatus == Download.COMPLETED) {
      return downloadsRepository.getDownload(md5)
          .first()
          .doOnNext(download -> removeAppDownloader(md5))
          .takeUntil(download -> download.getOverallProgress() == Download.COMPLETED);
    }
    return appDownloader.observeDownloadProgress()
        .flatMap(appDownloadStatus -> downloadsRepository.getDownload(appDownloadStatus.getMd5())
            .first()
            .flatMap(download -> updateDownload(download, appDownloadStatus)))
        .filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .doOnNext(download -> removeAppDownloader(download.getMd5()))
        .takeUntil(download -> download.getOverallDownloadStatus() == Download.COMPLETED);
  }

  private void removeAppDownloader(String md5) {
    AppDownloader appDownloader = appDownloaderMap.remove(md5);
    if (appDownloader != null) {
      appDownloader.removeAppDownload();
      appDownloader.stop();
    }
  }

  private Observable<Download> updateDownload(Download download,
      AppDownloadStatus appDownloadStatus) {
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

  private Observable<AppDownloader> getAppDownloader(Download download) {
    AppDownloader appDownloader = appDownloaderMap.get(download.getMd5());
    if (appDownloader != null) {
      return Observable.just(appDownloader);
    }
    return Observable.just(createAppDownloadManager(download));
  }

  private boolean isDownloadCompleted(Download download) {
    return download.getOverallProgress() == 100 && haveFilesBeenMoved(download);
  }

  private boolean haveFilesBeenMoved(Download download) {
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (!FileUtils.fileExists(getFilePathFromFileType(fileToDownload))) {
        return false;
      }
    }
    return true;
  }

  private void handleStartDownload(AppDownloader appDownloader, Download download) {
    if (isDownloadCompleted(download)) {
      download.setOverallDownloadStatus(Download.COMPLETED);
      downloadsRepository.save(download);
    } else {
      appDownloader.startAppDownload();
    }
  }

  @NonNull public String getFilePathFromFileType(FileToDownload fileToDownload) {
    String path;
    switch (fileToDownload.getFileType()) {
      case FileToDownload.APK:
        path = apkPath;
        break;
      case FileToDownload.OBB:
        path = obbPath + fileToDownload.getPackageName() + "/";
        break;
      case FileToDownload.GENERIC:
      default:
        path = cachePath;
        break;
    }
    return path;
  }
}
