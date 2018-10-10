package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.RealmList;
import java.util.HashMap;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AptoideDownloadManager implements DownloadManager {

  private final String cachePath;
  private final String apkPath;
  private final String obbPath;
  private final DownloadAppMapper downloadAppMapper;
  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;
  private DownloadStatusMapper downloadStatusMapper;
  private AppDownloaderProvider appDownloaderProvider;
  private Subscription dispatchDownloadsSubscription;
  private FileUtils fileUtils;

  public AptoideDownloadManager(DownloadsRepository downloadsRepository,
      DownloadStatusMapper downloadStatusMapper, String cachePath, String apkPath, String obbPath,
      DownloadAppMapper downloadAppMapper, AppDownloaderProvider appDownloaderProvider,
      FileUtils fileUtils) {
    this.downloadsRepository = downloadsRepository;
    this.downloadStatusMapper = downloadStatusMapper;
    this.cachePath = cachePath;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
    this.downloadAppMapper = downloadAppMapper;
    this.appDownloaderProvider = appDownloaderProvider;
    this.fileUtils = fileUtils;
    appDownloaderMap = new HashMap<>();
  }

  public synchronized void start() {
    dispatchDownloadsSubscription = (downloadsRepository.getInProgressDownloadsList()
        .filter(List::isEmpty)
        .flatMap(__ -> downloadsRepository.getInQueueDownloads())
        .filter(downloads -> !downloads.isEmpty())
        .first()
        .map(downloads -> downloads.get(0))
        .flatMap(download -> getAppDownloader(download.getMd5()).doOnNext(
            AppDownloader::startAppDownload)
            .flatMap(this::handleDownloadProgress))
        .doOnError(throwable -> throwable.printStackTrace())).retry()
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
    })
        .subscribeOn(Schedulers.computation());
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
            .filter(download -> download != null || isFileMissingFromCompletedDownload(download))
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
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
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
      for (FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
          downloadState = Download.FILE_MISSING;
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
        .doOnNext(download -> downloadsRepository.save(download))
        .filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .doOnNext(download -> handleCompletedDownload(download))
        .subscribeOn(Schedulers.io());
  }

  private void handleCompletedDownload(Download download) {
    moveCompletedDownloadFiles(download.getFilesToDownload());
    removeAppDownloader(download.getMd5());
  }

  private void removeAppDownloader(String md5) {
    AppDownloader appDownloader = appDownloaderMap.get(md5);
    if (appDownloader != null) {
      appDownloader.stop();
      appDownloaderMap.remove(md5);
    }
  }

  private void moveCompletedDownloadFiles(RealmList<FileToDownload> filesToDownload) {
    for (FileToDownload fileToDownload : filesToDownload) {
      String newFilePath = getFilePathFromFileType(fileToDownload);
      fileUtils.copyFile(cachePath, newFilePath, fileToDownload.getFileName());
      fileToDownload.setPath(newFilePath);
    }
  }

  @NonNull private String getFilePathFromFileType(FileToDownload fileToDownload) {
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
    return Observable.just(download);
  }

  private Observable<AppDownloader> getAppDownloader(String md5) {
    return Observable.just(appDownloaderMap.get(md5));
  }
}
