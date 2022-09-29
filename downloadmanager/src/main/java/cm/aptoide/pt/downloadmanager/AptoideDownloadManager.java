package cm.aptoide.pt.downloadmanager;

import android.util.Log;
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;
import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

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
  private final FileUtils fileUtils;
  private final PathProvider pathProvider;
  private final CompositeDisposable downloadsSubscription;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper, String cachePath,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      FileUtils fileUtils, PathProvider pathProvider) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.cachePath = cachePath;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
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

  @Override public Completable startDownload(DownloadEntity download) {
    return Completable.fromAction(() -> {
          download.setOverallDownloadStatus(DownloadEntity.IN_QUEUE);
          download.setTimeStamp(System.currentTimeMillis());
        })
        .andThen(downloadsRepository.save(download))
        .doOnComplete(
            () -> appDownloaderMap.put(download.getMd5(), createAppDownloadManager(download)))
        .doOnError(Throwable::printStackTrace)
        .subscribeOn(Schedulers.io());
  }

  @Override public Observable<DownloadEntity> getDownloadAsObservable(String md5) {
    return downloadsRepository.getDownloadAsObservable(md5)
        .flatMap(download -> {
          if (download == null || isFileMissingFromCompletedDownload(download)) {
            return Observable.error(new DownloadNotFoundException());
          } else {
            return Observable.just(download);
          }
        })
        .takeUntil(
            storedDownload -> storedDownload.getOverallDownloadStatus() == DownloadEntity.COMPLETED)
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<DownloadEntity> getDownloadAsSingle(String md5) {
    return downloadsRepository.getDownloadAsSingle(md5)
        .subscribeOn(Schedulers.io())
        .flatMap(download -> {
          if (download == null || isFileMissingFromCompletedDownload(download)) {
            return Single.error(new DownloadNotFoundException());
          } else {
            return Single.just(download);
          }
        });
  }

  @Override public Observable<List<DownloadEntity>> getDownloadsList() {
    return downloadsRepository.getAllDownloads();
  }

  @Override public Observable<DownloadEntity> getCurrentInProgressDownload() {
    return downloadsRepository.getInProgressDownloadsList()
        .subscribeOn(Schedulers.io())
        .flatMapIterable(downloads -> downloads)
        .filter(download -> download.getOverallDownloadStatus() == DownloadEntity.PROGRESS);
  }

  @Override public Observable<List<DownloadEntity>> getCurrentActiveDownloads() {
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
            .filter(download -> getStateIfFileExists(download) == DownloadEntity.FILE_MISSING)
            .flatMapCompletable(download -> downloadsRepository.remove(download.getMd5())));
  }

  @Override public Observable<DownloadEntity> getCompletedDownload(@NotNull String packageName) {
    return downloadsRepository.getCompletedDownload(packageName);
  }

  private void dispatchDownloads() {
    downloadsSubscription.add(downloadsRepository.getInProgressDownloadsList()
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .doOnError(Throwable::printStackTrace)
        .retry()
        .throttleLast(750, TimeUnit.MILLISECONDS)
        .doOnNext(downloads -> Logger.getInstance()
            .d(TAG, "Downloads in Progress " + downloads.size()))
        .filter(List::isEmpty)
        .flatMap(__ -> downloadsRepository.getInQueueDownloads())
        .doOnNext(list -> Log.d(TAG, "dispatchDownloads: emitted after inqueue " + list.size()))
        .distinctUntilChanged()
        .doOnError(Throwable::printStackTrace)
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
        }, throwable1 -> throwable1.printStackTrace()));
  }

  private void moveFilesFromCompletedDownloads() {
    downloadsSubscription.add(downloadsRepository.getWaitingToMoveFilesDownloads()
        .subscribeOn(Schedulers.io())
        .filter(downloads -> !downloads.isEmpty())
        .flatMapIterable(download -> download)
        .flatMapCompletable(
            download -> moveCompletedDownloadFiles(download).onErrorResumeNext(throwable -> {
              throwable.printStackTrace();
              download.setDownloadError(DownloadEntity.GENERIC_ERROR);
              download.setOverallDownloadStatus(DownloadEntity.ERROR);
              return downloadsRepository.save(download);
            }))
        .retry()
        .subscribe(() -> {
        }, Throwable::printStackTrace));
  }

  public Completable moveCompletedDownloadFiles(DownloadEntity download) {
    return Completable.fromAction(() -> {
          for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
            String newFilePath = pathProvider.getFilePathFromFileType(fileToDownload);
            if (!FileUtils.fileExists(
                pathProvider.getFilePathFromFileType(fileToDownload) + fileToDownload.getFileName())) {
              Logger.getInstance()
                  .d(TAG, "trying to move file : "
                      + fileToDownload.getFileName()
                      + " "
                      + fileToDownload.getPackageName());
              fileUtils.copyFile(fileToDownload.getPath(), newFilePath, fileToDownload.getFileName());
              fileToDownload.setPath(newFilePath);
            } else {
              fileToDownload.setPath(newFilePath);
              Logger.getInstance()
                  .d(TAG, "tried moving file: "
                      + fileToDownload.getFileName()
                      + " "
                      + fileToDownload.getPackageName()
                      + " but it was already moved. The path that we were trying to move to was "
                      + fileToDownload.getFilePath());
            }
          }
          download.setOverallDownloadStatus(DownloadEntity.COMPLETED);
        })
        .subscribeOn(Schedulers.io())
        .andThen(downloadsRepository.save(download));
  }

  private void removeDownloadFiles(DownloadEntity download) {
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      FileUtils.removeFile(fileToDownload.getFilePath());
      FileUtils.removeFile(cachePath + fileToDownload.getFileName() + ".temp");
    }
  }

  private AppDownloader createAppDownloadManager(DownloadEntity download) {
    DownloadApp downloadApp = downloadAppMapper.mapDownload(download);
    return appDownloaderProvider.getAppDownloader(downloadApp);
  }

  private boolean isFileMissingFromCompletedDownload(DownloadEntity download) {
    return download.getOverallDownloadStatus() == DownloadEntity.COMPLETED
        && getStateIfFileExists(download) == DownloadEntity.FILE_MISSING;
  }

  private int getStateIfFileExists(DownloadEntity download) {
    int downloadState = DownloadEntity.COMPLETED;
    if (download.getOverallDownloadStatus() == DownloadEntity.PROGRESS) {
      downloadState = DownloadEntity.PROGRESS;
    } else {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
          downloadState = DownloadEntity.FILE_MISSING;
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

  private Observable<DownloadEntity> handleDownloadProgress(AppDownloader appDownloader) {
    return appDownloader.observeDownloadProgress()
        .subscribeOn(Schedulers.io())
        .flatMap(
            appDownloadStatus -> downloadsRepository.getDownloadAsSingle(appDownloadStatus.getMd5())
                .flatMapObservable(download -> updateDownload(download, appDownloadStatus).andThen(
                    Observable.just(download))))
        .doOnNext(download -> {
          if (download.getOverallDownloadStatus() == DownloadEntity.PROGRESS) {
            //downloadAnalytics.startProgress(download);
          }
        })
        .filter(
            download -> download.getOverallDownloadStatus() == DownloadEntity.WAITING_TO_MOVE_FILES)
        .doOnNext(download -> removeAppDownloader(download.getMd5()))
        .takeUntil(download -> download.getOverallDownloadStatus()
            == DownloadEntity.WAITING_TO_MOVE_FILES);
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

  private Completable updateDownload(DownloadEntity download, AppDownloadStatus appDownloadStatus) {
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
    if (appDownloadStatus.getDownloadStatus()
        .equals(AppDownloadStatus.AppDownloadState.ERROR_MD5_DOES_NOT_MATCH)) {
      removeDownloadFiles(download);
    }
    return downloadsRepository.save(download);
  }

  private Observable<AppDownloader> getAppDownloader(DownloadEntity download) {
    return Observable.just(appDownloaderMap.get(download.getMd5()))
        .map(appDownloader -> {
          if (appDownloader == null) {
            return createAppDownloadManager(download);
          }
          return appDownloader;
        });
  }
}
