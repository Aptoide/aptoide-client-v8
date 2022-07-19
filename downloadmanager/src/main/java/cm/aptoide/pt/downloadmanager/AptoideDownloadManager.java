package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomFileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AptoideDownloadManager implements DownloadManager {

  private static final String TAG = "AptoideDownloadManager";
  private final DownloadAppMapper downloadAppMapper;
  private final String cachePath;
  private final DownloadsRepository downloadsRepository;
  private final HashMap<String, AppDownloader> appDownloaderMap;
  private final DownloadStatusMapper downloadStatusMapper;
  private final AppDownloaderProvider appDownloaderProvider;
  private final DownloadAnalytics downloadAnalytics;
  private final FileUtils fileUtils;
  private final PathProvider pathProvider;
  private final CompositeDisposable downloadsSubscription;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper, String cachePath,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      DownloadAnalytics downloadAnalytics, FileUtils fileUtils, PathProvider pathProvider) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.cachePath = cachePath;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
    this.downloadAnalytics = downloadAnalytics;
    this.fileUtils = fileUtils;
    this.pathProvider = pathProvider;
    this.appDownloaderMap = new HashMap<>();
    this.downloadsSubscription = new CompositeDisposable();
  }

  public synchronized void start() {
    dispatchDownloads();

    moveFilesFromCompletedDownloads();
  }

  @Override public void stop() {
    downloadsSubscription.clear();
  }

  @Override public Completable startDownload(RoomDownload download) {
    return Completable.fromAction(() -> {
          download.setOverallDownloadStatus(RoomDownload.IN_QUEUE);
          download.setTimeStamp(System.currentTimeMillis());
        })
        .andThen(downloadsRepository.save(download))
        .doOnComplete(
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

  @Override public Single<RoomDownload> getDownloadsByMd5(String md5) {
    // TODO: 7/19/22 this was an observable does this still need to exist ?
    return downloadsRepository.getDownloadListByMd5(md5)
        .flatMapIterable(downloads -> downloads)
        .filter(download -> download != null && !isFileMissingFromCompletedDownload(download))
        .toList()
        .map(downloads -> {
          if (downloads.isEmpty()) {
            return null;
          } else {
            return downloads.get(0);
          }
        })
        .doOnSuccess(download -> Logger.getInstance()
            .d(TAG, "passing a download : "));
  }

  @Override public Observable<List<RoomDownload>> getDownloadsList() {
    return downloadsRepository.getAllDownloads();
  }

  @Override public Observable<RoomDownload> getCurrentInProgressDownload() {
    return downloadsRepository.getInProgressDownloadsList()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> download.getOverallDownloadStatus() == RoomDownload.PROGRESS);
    // TODO: 7/19/22 get directly from the database
  }

  @Override public Observable<List<RoomDownload>> getCurrentActiveDownloads() {
    return downloadsRepository.getCurrentActiveDownloads();
  }

  @Override public Completable removeDownload(String md5) {
    return downloadsRepository.getDownloadAsSingle(md5)
        .flatMapCompletable(download -> getAppDownloader(download).flatMapCompletable(
            appDownloader -> appDownloader.removeAppDownload()
                .doOnComplete(() -> removeDownloadFiles(download))))
        .andThen(downloadsRepository.remove(md5));
  }

  @Override public Completable invalidateDatabase() {
    return getDownloadsList().first(new ArrayList<>())
        .flatMapCompletable(downloadsList -> Observable.fromIterable(downloadsList)
            .filter(download -> getStateIfFileExists(download) == RoomDownload.FILE_MISSING)
            .flatMapCompletable(download -> downloadsRepository.remove(download.getMd5())));
  }

  private void dispatchDownloads() {
    downloadsSubscription.add(downloadsRepository.getInProgressDownloadsList()
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
        .throttleLast(750, TimeUnit.MILLISECONDS)
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Downloads in Progress " + downloads.size()))
        .filter(List::isEmpty)
        .flatMap(__ -> downloadsRepository.getInQueueDownloads())
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
        }, Throwable::printStackTrace));
  }

  private void moveFilesFromCompletedDownloads() {
    downloadsSubscription.add(downloadsRepository.getWaitingToMoveFilesDownloads()
        .filter(downloads -> !downloads.isEmpty())
        .flatMapIterable(download -> download)
        .flatMapCompletable(
            download -> moveCompletedDownloadFiles(download).onErrorResumeNext(throwable -> {
              throwable.printStackTrace();
              download.setDownloadError(RoomDownload.GENERIC_ERROR);
              download.setOverallDownloadStatus(RoomDownload.ERROR);
              return downloadsRepository.save(download);
            }))
        .retry()
        .subscribe(() -> {
        }, Throwable::printStackTrace));
  }

  public Completable moveCompletedDownloadFiles(RoomDownload download) {
    return Completable.fromAction(() -> {
          for (final RoomFileToDownload roomFileToDownload : download.getFilesToDownload()) {
            String newFilePath = pathProvider.getFilePathFromFileType(roomFileToDownload);
            if (!FileUtils.fileExists(pathProvider.getFilePathFromFileType(roomFileToDownload)
                + roomFileToDownload.getFileName())) {
              Logger.getInstance()
                  .d(TAG, "trying to move file : "
                      + roomFileToDownload.getFileName()
                      + " "
                      + roomFileToDownload.getPackageName());
              fileUtils.copyFile(roomFileToDownload.getPath(), newFilePath,
                  roomFileToDownload.getFileName());
              roomFileToDownload.setPath(newFilePath);
            } else {
              roomFileToDownload.setPath(newFilePath);
              Logger.getInstance()
                  .d(TAG, "tried moving file: "
                      + roomFileToDownload.getFileName()
                      + " "
                      + roomFileToDownload.getPackageName()
                      + " but it was already moved. The path that we were trying to move to was "
                      + roomFileToDownload.getFilePath());
            }
          }
          download.setOverallDownloadStatus(RoomDownload.COMPLETED);
        })
        .andThen(downloadsRepository.save(download));
  }

  private void removeDownloadFiles(RoomDownload download) {
    for (final RoomFileToDownload fileToDownload : download.getFilesToDownload()) {
      FileUtils.removeFile(fileToDownload.getFilePath());
      FileUtils.removeFile(cachePath + fileToDownload.getFileName() + ".temp");
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
        .flatMap(
            appDownloadStatus -> downloadsRepository.getDownloadAsSingle(appDownloadStatus.getMd5())
                .flatMapObservable(download -> updateDownload(download, appDownloadStatus).andThen(
                    Observable.just(download))))
        .doOnNext(download -> {
          if (download.getOverallDownloadStatus() == RoomDownload.PROGRESS) {
            downloadAnalytics.startProgress(download);
          }
        })
        .filter(
            download -> download.getOverallDownloadStatus() == RoomDownload.WAITING_TO_MOVE_FILES)
        .doOnNext(download -> downloadAnalytics.onDownloadComplete(download.getMd5(),
            download.getPackageName(), download.getVersionCode()))
        .doOnNext(download -> removeAppDownloader(download.getMd5()))
        .takeUntil(
            download -> download.getOverallDownloadStatus() == RoomDownload.WAITING_TO_MOVE_FILES);
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
