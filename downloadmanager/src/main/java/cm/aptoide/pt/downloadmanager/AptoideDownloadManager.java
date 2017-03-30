package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.interfaces.Analytics;
import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadNotificationActionsInterface;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.OkHttpClient;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager {

  public static final String FILE_MD5_EXTRA = "APTOIDE_APPID_EXTRA";
  public static final String DOWNLOADMANAGER_ACTION_PAUSE =
      "cm.aptoide.downloadmanager.action.pause"; // click on pause button

  public static final String DOWNLOADMANAGER_ACTION_OPEN = "cm.aptoide.downloadmanager.action.open";
  // open downloads tabs
  public static final String DOWNLOADMANAGER_ACTION_START_DOWNLOAD =
      "cm.aptoide.downloadmanager.action.start.download";
  public static final String DOWNLOADMANAGER_ACTION_NOTIFICATION =
      "cm.aptoide.downloadmanager.action.notification"; //open app view
  static public final int PROGRESS_MAX_VALUE = 100;
  private static final String TAG = AptoideDownloadManager.class.getSimpleName();
  private static final int VALUE_TO_CONVERT_MB_TO_BYTES = 1024 * 1024;

  /***********
   * Paths
   *****************/
  static String DOWNLOADS_STORAGE_PATH;
  static String APK_PATH;
  static String OBB_PATH;
  private static AptoideDownloadManager instance;
  private static Context context;
  private boolean isDownloading = false;
  private boolean isPausing = false;
  @Getter(AccessLevel.MODULE) private DownloadNotificationActionsInterface
      downloadNotificationActionsInterface;
  @Getter private DownloadSettingsInterface settingsInterface;
  private DownloadAccessor downloadAccessor;
  private CacheManager cacheHelper;
  private FileUtils fileUtils;
  private Analytics analytics;

  public static Context getContext() {
    return context;
  }

  public static AptoideDownloadManager getInstance() {
    if (instance == null) {
      instance = new AptoideDownloadManager();
    }
    return instance;
  }

  public void initDownloadService(Context context) {
    AptoideDownloadManager.context = context;
    createDownloadDirs();
  }

  private void createDownloadDirs() {
    FileUtils.createDir(APK_PATH);
    FileUtils.createDir(OBB_PATH);
    //FileUtils.createDir(GENERIC_PATH);
  }

  /**
   * @param download info about the download to be made.
   *
   * @return Observable to be subscribed if download updates needed or null if download is done
   * already
   *
   * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this
   * exception will be thrown with the cause in the detail
   * message.
   */
  public Observable<Download> startDownload(Download download) throws IllegalArgumentException {
    return getDownloadStatus(download.getMd5()).first().flatMap(status -> {
      if (status == Download.COMPLETED) {
        return Observable.just(download);
      } else {
        Observable.fromCallable(() -> {
          startNewDownload(download);
          return null;
        }).subscribeOn(Schedulers.computation()).subscribe(o -> {
        }, throwable -> CrashReport.getInstance().log(throwable));
        return getDownload(download.getMd5());
      }
    });
  }

  private void startNewDownload(Download download) {
    download.setOverallDownloadStatus(Download.IN_QUEUE);
    //commented to prevent the ui glitch with "0" value
    // (trusting in progress value from outside can be dangerous)
    //		download.setOverallProgress(0);
    download.setTimeStamp(System.currentTimeMillis());
    downloadAccessor.save(download);

    startNextDownload();
  }

  /**
   * Observe changes to a download. This observable never completes it will emmit items whenever
   * the download state changes.
   *
   * @return observable for download state changes.
   */
  public Observable<Download> getDownload(String md5) {
    return downloadAccessor.get(md5).flatMap(download -> {
      if (download == null || (download.getOverallDownloadStatus() == Download.COMPLETED
          && getInstance().getStateIfFileExists(download) == Download.FILE_MISSING)) {
        return Observable.error(new DownloadNotFoundException());
      } else {
        return Observable.just(download);
      }
    }).takeUntil(storedDownload -> storedDownload.getOverallDownloadStatus() == Download.COMPLETED);
  }

  public Observable<List<Download>> getAsListDownload(String md5) {
    return downloadAccessor.getAsList(md5).map(downloads -> {
      for (int i = 0; i < downloads.size(); i++) {
        Download download = downloads.get(i);
        if (download == null || (download.getOverallDownloadStatus() == Download.COMPLETED
            && getInstance().getStateIfFileExists(download) == Download.FILE_MISSING)) {
          downloads.remove(i);
          i--;
        }
      }
      return downloads;
    }).distinctUntilChanged();
  }

  @NonNull @Download.DownloadState private int getStateIfFileExists(Download downloadToCheck) {
    @Download.DownloadState int downloadStatus = Download.COMPLETED;
    if (downloadToCheck.getOverallDownloadStatus() == Download.PROGRESS) {
      downloadStatus = Download.PROGRESS;
    } else {
      for (final FileToDownload fileToDownload : downloadToCheck.getFilesToDownload()) {
        if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
          downloadStatus = Download.FILE_MISSING;
          break;
        }
      }
    }
    return downloadStatus;
  }

  public Observable<Download> getCurrentDownload() {
    return getDownloads().flatMapIterable(downloads -> downloads)
        .filter(downloads -> downloads.getOverallDownloadStatus() == Download.PROGRESS);
  }

  public Observable<List<Download>> getDownloads() {
    return downloadAccessor.getAll();
  }

  public Observable<List<Download>> getCurrentDownloads() {
    return downloadAccessor.getRunningDownloads();
  }

  /**
   * Pause all the downloads
   */
  public void pauseAllDownloads() {
    FileDownloader.getImpl().pauseAll();
    isPausing = true;

    downloadAccessor.getRunningDownloads()
        .first()
        .doOnUnsubscribe(() -> isPausing = false)
        .subscribe(downloads -> {
          for (int i = 0; i < downloads.size(); i++) {
            downloads.get(i).setOverallDownloadStatus(Download.PAUSED);
          }
          downloadAccessor.save(downloads);
          Logger.d(TAG, "Downloads paused");
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  private Observable<Integer> getDownloadStatus(String md5) {
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

  public void init(Context context,
      DownloadNotificationActionsInterface downloadNotificationActionsInterface,
      DownloadSettingsInterface settingsInterface, DownloadAccessor downloadAccessor,
      CacheManager cacheHelper, FileUtils fileUtils, OkHttpClient.Builder httpClientBuilder,
      Analytics analytics) {
    this.analytics = analytics;

    FileDownloader.init(context, new DownloadMgrInitialParams.InitCustomMaker().connectionCreator(
        new OkHttp3Connection.Creator(httpClientBuilder)));
    this.downloadNotificationActionsInterface = downloadNotificationActionsInterface;
    this.settingsInterface = settingsInterface;
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;

    DOWNLOADS_STORAGE_PATH = settingsInterface.getDownloadDir();
    APK_PATH = DOWNLOADS_STORAGE_PATH + "apks/";
    OBB_PATH = DOWNLOADS_STORAGE_PATH + "obb/";
    this.downloadAccessor = downloadAccessor;
  }

  void currentDownloadFinished() {
    startNextDownload();
  }

  synchronized void startNextDownload() {
    if (!isDownloading && !isPausing) {
      isDownloading = true;
      getNextDownload().first().subscribe(download -> {
        if (download != null) {
          new DownloadTask(downloadAccessor, download, fileUtils, analytics).startDownload();
          Logger.d(TAG, "Download with md5 " + download.getMd5() + " started");
        } else {
          isDownloading = false;
          cacheHelper.cleanCache()
              .subscribe(cleanedSize -> Logger.d(TAG,
                  "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
                  throwable -> {
                    CrashReport.getInstance().log(throwable);
                  });
        }
      }, throwable -> throwable.printStackTrace());
    }
  }

  public Observable<Download> getNextDownload() {
    return downloadAccessor.getInQueueSortedDownloads().map(downloads -> {
      if (downloads == null || downloads.size() <= 0) {
        return null;
      } else {
        return downloads.get(0);
      }
    });
  }

  /**
   * check if there is any download in progress
   *
   * @return true if there is at least 1 download in progress, false otherwise
   */
  public boolean isDownloading() {
    return isDownloading;
  }

  public void setDownloading(boolean downloading) {
    isDownloading = downloading;
  }

  public void removeDownload(String md5) {
    Observable.fromCallable(() -> pauseDownload(md5))
        .flatMap(paused -> downloadAccessor.get(md5))
        .first(download -> download.getOverallDownloadStatus() == Download.PAUSED)
        .doOnNext(download -> {
          for (int i = 0; i < download.getFilesToDownload().size(); i++) {
            final FileToDownload fileToDownload = download.getFilesToDownload().get(i);
            FileDownloader.getImpl()
                .clear(fileToDownload.getDownloadId(), fileToDownload.getFilePath());
          }
        })
        .subscribe(download -> {
          deleteDownloadFiles(download);
          deleteDownloadFromDb(download.getMd5());
        }, throwable -> {
          if (throwable instanceof NullPointerException) {
            Logger.d(TAG, "Download item was null, are you pressing on remove button too fast?");
          } else {
            throwable.printStackTrace();
          }
        });
  }

  public Completable pauseDownloadSync(String md5) {
    return internalPause(md5).toCompletable();
  }

  @NonNull private Observable<Download> internalPause(String md5) {
    return downloadAccessor.get(md5).first().map(download -> {
      download.setOverallDownloadStatus(Download.PAUSED);
      downloadAccessor.save(download);
      for (int i = download.getFilesToDownload().size() - 1; i >= 0; i--) {
        FileDownloader.getImpl().pause(download.getFilesToDownload().get(i).getDownloadId());
      }
      return download;
    });
  }

  public Void pauseDownload(String md5) {
    internalPause(md5).subscribe(download -> {
      Logger.d(TAG, "Download with " + md5 + " paused");
    }, throwable -> {
      if (throwable instanceof DownloadNotFoundException) {
        Logger.d(TAG, "there are no download to pause with the md5: " + md5);
      } else {
        throwable.printStackTrace();
      }
    });
    return null;
  }

  private void deleteDownloadFiles(Download download) {
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (download.getOverallDownloadStatus() == Download.COMPLETED) {
        FileUtils.removeFile(fileToDownload.getFilePath());
      } else {
        FileUtils.removeFile(DOWNLOADS_STORAGE_PATH + fileToDownload.getFileName() + ".temp");
      }
    }
  }

  private void deleteDownloadFromDb(String md5) {
    downloadAccessor.delete(md5);
  }

  public Observable<Void> invalidateDatabase() {
    return getDownloads().first()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> getStateIfFileExists(download) == Download.FILE_MISSING)
        .map(download -> {
          downloadAccessor.delete(download.getMd5());
          return null;
        })
        .toList()
        .flatMap(success -> Observable.just(null));
  }
}
