/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import io.realm.RealmList;
import java.io.File;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/13/16.
 */
class DownloadTask extends FileDownloadLargeFileListener {

  public static final int RETRY_TIMES = 3;
  private static final int INTERVAL = 1000;    //interval between progress updates
  private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
  private static final int FILE_NOTFOUND_HTTP_ERROR = 404;
  private static final String TAG = DownloadTask.class.getSimpleName();
  private final Download download;
  private final String md5;
  private final DownloadAccessor downloadAccessor;
  private final FileUtils fileUtils;
  private final AptoideDownloadManager downloadManager;
  /**
   * this boolean is used to change between serial and parallel download (in this downloadTask) the
   * default value is
   * true
   */
  boolean isSerial = true;
  private ConnectableObservable<Download> observable;
  private Analytics analytics;
  private String apkPath;
  private String obbPath;
  private String genericPath;
  private FileDownloader fileDownloader;

  DownloadTask(DownloadAccessor downloadAccessor, Download download, FileUtils fileUtils,
      Analytics analytics, AptoideDownloadManager downloadManager, String apkPath, String obbPath,
      String genericPath, FileDownloader fileDownloader) {
    this.analytics = analytics;
    this.download = download;
    this.md5 = download.getMd5();
    this.downloadAccessor = downloadAccessor;
    this.fileUtils = fileUtils;
    this.downloadManager = downloadManager;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
    this.genericPath = genericPath;
    this.fileDownloader = fileDownloader;
    this.observable = Observable.interval(INTERVAL / 4, INTERVAL, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .takeUntil(integer1 -> download.getOverallDownloadStatus() != Download.PROGRESS
            && download.getOverallDownloadStatus() != Download.IN_QUEUE
            && download.getOverallDownloadStatus() != Download.PENDING)
        .filter(aLong1 -> download.getOverallDownloadStatus() == Download.PROGRESS
            || download.getOverallDownloadStatus() == Download.COMPLETED)
        .map(aLong -> updateProgress())
        .filter(updatedDownload -> {
          if (updatedDownload.getOverallProgress() <= AptoideDownloadManager.PROGRESS_MAX_VALUE
              && download.getOverallDownloadStatus() == Download.PROGRESS) {
            if (updatedDownload.getOverallProgress() == AptoideDownloadManager.PROGRESS_MAX_VALUE
                && download.getOverallDownloadStatus() != Download.COMPLETED) {
              setDownloadStatus(Download.COMPLETED, download);
              analytics.onDownloadComplete(download);
              downloadManager.currentDownloadFinished();
            }
            return true;
          } else {
            return false;
          }
        })
        .publish();
  }

  /**
   * Update the overall download progress. It updates the value on database and in memory list
   *
   * @return new current progress
   */
  @NonNull private Download updateProgress() {
    if (download.getOverallProgress() >= AptoideDownloadManager.PROGRESS_MAX_VALUE
        || download.getOverallDownloadStatus() != Download.PROGRESS) {
      return download;
    }

    int progress = 0;
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      progress += fileToDownload.getProgress();
    }
    download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload()
        .size()));
    saveDownloadInDb(download);
    Logger.d(TAG, "Download: " + download.getMd5() + " Progress: " + download.getOverallProgress());
    return download;
  }

  private void setDownloadStatus(@Download.DownloadState int status, Download download) {
    setDownloadStatus(status, download, null);
  }

  private synchronized void saveDownloadInDb(Download download) {
    Observable.fromCallable(() -> {
      downloadAccessor.save(download);
      return null;
    })
        .subscribeOn(Schedulers.io())
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private void setDownloadStatus(@Download.DownloadState int status, Download download,
      @Nullable BaseDownloadTask task) {
    if (task != null) {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (fileToDownload.getDownloadId() == task.getId()) {
          fileToDownload.setStatus(status);
        }
      }
    }

    this.download.setOverallDownloadStatus(status);
    saveDownloadInDb(download);
    if (status == Download.PROGRESS || status == Download.PENDING) {
      downloadManager.setDownloading(true);
    } else {
      downloadManager.setDownloading(false);
    }
  }

  @Override protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    setDownloadStatus(Download.PENDING, download, task);
  }

  @Override protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    pending(task, (long) soFarBytes, (long) totalBytes);
    setDownloadStatus(Download.PENDING, download, task);
  }

  @Override protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (fileToDownload.getDownloadId() == task.getId()) {
        //sometimes to totalBytes = 0, i believe that's when a 301(Moved Permanently) http error occurs
        if (totalBytes > 0) {
          fileToDownload.setProgress((int) Math.floor(
              (float) soFarBytes / totalBytes * AptoideDownloadManager.PROGRESS_MAX_VALUE));
        } else {
          fileToDownload.setProgress(0);
        }
      }
    }
    this.download.setDownloadSpeed(task.getSpeed() * 1024);
    if (download.getOverallDownloadStatus() != Download.PROGRESS) {
      setDownloadStatus(Download.PROGRESS, download, task);
    }
  }

  @Override protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    progress(task, (long) soFarBytes, (long) totalBytes);
  }

  @Override protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    setDownloadStatus(Download.PAUSED, download, task);
    downloadManager.currentDownloadFinished();
  }

  @Override protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    paused(task, (long) soFarBytes, (long) totalBytes);
  }

  @Override protected void blockComplete(BaseDownloadTask task) {

  }

  @Override protected void completed(BaseDownloadTask task) {
    Observable.from(download.getFilesToDownload())
        .filter(file -> file.getDownloadId() == task.getId())
        .flatMap(file -> {
          file.setStatus(Download.COMPLETED);
          for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
            if (fileToDownload.getStatus() != Download.COMPLETED) {
              file.setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE);
              return Observable.just(null);
            }
          }
          return checkMd5AndMoveFileToRightPlace(download).doOnNext(fileMoved -> {
            if (fileMoved) {
              Logger.d(TAG, "Download md5 match");
              file.setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE);
            } else {
              Logger.e(TAG, "Download md5 is not correct");
              downloadManager.deleteDownloadlFiles(download);
              download.setDownloadError(Download.GENERIC_ERROR);
              setDownloadStatus(Download.ERROR, download, task);
            }
          });
        })
        .doOnUnsubscribe(() -> downloadManager.setDownloading(false))
        .subscribeOn(Schedulers.io())
        .subscribe(success -> saveDownloadInDb(download),
            throwable -> setDownloadStatus(Download.ERROR, download));
    download.setDownloadSpeed(task.getSpeed() * 1024);
  }

  @Override protected void error(BaseDownloadTask task, Throwable e) {
    stopDownloadQueue(download);
    if (e instanceof FileDownloadHttpException
        && ((FileDownloadHttpException) e).getCode() == FILE_NOTFOUND_HTTP_ERROR) {
      Logger.d(TAG, "File not found on link: " + task.getUrl());
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (TextUtils.equals(fileToDownload.getLink(), task.getUrl()) && !TextUtils.isEmpty(
            fileToDownload.getAltLink())) {
          fileToDownload.setLink(fileToDownload.getAltLink());
          fileToDownload.setAltLink(null);
          downloadAccessor.save(download);
          startDownload();
          return;
        }
      }
    } else {
      Logger.d(TAG, "Error on download: " + download.getMd5());
      // Apparently throwable e can be null.
      if (e != null) {
        e.printStackTrace();
      }
      if (analytics != null) {
        analytics.onError(download, e);
      }
    }
    if (e instanceof FileDownloadOutOfSpaceException) {
      download.setDownloadError(Download.NOT_ENOUGH_SPACE_ERROR);
    } else {
      download.setDownloadError(Download.GENERIC_ERROR);
    }
    setDownloadStatus(Download.ERROR, download, task);
    downloadManager.currentDownloadFinished();
  }

  @Override protected void warn(BaseDownloadTask task) {
    setDownloadStatus(Download.WARN, download, task);
  }

  /**
   * this method will pause all downloads listed on {@link Download#filesToDownload} without change
   * download state, the listener is removed in order to keep the download state, this means that
   * the "virtual" pause will not affect the download state
   */
  private void stopDownloadQueue(Download download) {
    //this try catch sucks
    try {
      for (int i = download.getFilesToDownload()
          .size() - 1; i >= 0; i--) {
        FileToDownload fileToDownload = download.getFilesToDownload()
            .get(i);
        fileDownloader.getStatus(fileToDownload.getDownloadId(), fileToDownload.getPath());
        int taskId = fileDownloader.replaceListener(fileToDownload.getDownloadId(), null);
        if (taskId != 0) {
          fileDownloader.pause(taskId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @throws IllegalArgumentException
   */
  public void startDownload() throws IllegalArgumentException {
    observable.connect();
    if (download.getFilesToDownload() != null) {

      RealmList<FileToDownload> filesToDownload = download.getFilesToDownload();
      FileToDownload fileToDownload = null;
      for (int i = 0; i < filesToDownload.size(); i++) {

        fileToDownload = filesToDownload.get(i);

        if (TextUtils.isEmpty(fileToDownload.getLink())) {
          throw new IllegalArgumentException("A link to download must be provided");
        }
        BaseDownloadTask baseDownloadTask = fileDownloader.create(fileToDownload.getLink())
            .setAutoRetryTimes(RETRY_TIMES);
        /*
         * Aptoide - events 2 : download
         * Get X-Mirror and add to the event
         */
        baseDownloadTask.addHeader(Constants.VERSION_CODE,
            String.valueOf(download.getVersionCode()));
        baseDownloadTask.addHeader(Constants.PACKAGE, download.getPackageName());
        baseDownloadTask.addHeader(Constants.FILE_TYPE, String.valueOf(i));
        /*
         * end
         */

        baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, this);
        if (fileToDownload.getFileName()
            .endsWith(".temp")) {
          fileToDownload.setFileName(fileToDownload.getFileName()
              .replace(".temp", ""));
        }
        fileToDownload.setDownloadId(baseDownloadTask.setListener(this)
            .setCallbackProgressTimes(AptoideDownloadManager.PROGRESS_MAX_VALUE)
            .setPath(genericPath + fileToDownload.getFileName())
            .asInQueueTask()
            .enqueue());
        fileToDownload.setPath(genericPath);
        fileToDownload.setFileName(fileToDownload.getFileName() + ".temp");
      }

      if (isSerial) {
        // To form a queue with the same queueTarget and execute them linearly
        fileDownloader.start(this, true);
      } else {
        // To form a queue with the same queueTarget and execute them in parallel
        fileDownloader.start(this, false);
      }
    }
    saveDownloadInDb(download);
  }

  private Observable<Boolean> checkMd5AndMoveFileToRightPlace(Download download) {
    return Observable.fromCallable(() -> {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        fileToDownload.setFileName(fileToDownload.getFileName()
            .replace(".temp", ""));
        if (!TextUtils.isEmpty(fileToDownload.getMd5())) {
          if (!TextUtils.equals(AptoideUtils.AlgorithmU.computeMd5(
              new File(genericPath + fileToDownload.getFileName())), fileToDownload.getMd5())) {
            return false;
          }
        }
        String newFilePath = getFilePathFromFileType(fileToDownload);
        fileUtils.copyFile(genericPath, newFilePath, fileToDownload.getFileName());
        fileToDownload.setPath(newFilePath);
      }
      return true;
    });
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
        path = genericPath;
        break;
    }
    return path;
  }

  public void setSerial(boolean isSerial) {
    this.isSerial = isSerial;
  }
}
