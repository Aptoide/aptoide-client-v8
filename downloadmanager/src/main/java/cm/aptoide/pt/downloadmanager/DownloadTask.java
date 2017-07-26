/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 5/13/16.
 */
class DownloadTask {

  public static final int RETRY_TIMES = 3;
  private static final int FILE_NOTFOUND_HTTP_ERROR = 404;
  private static final String TAG = DownloadTask.class.getSimpleName();
  public final int progressMaxValue;
  private final Download download;
  private final DownloadAccessor downloadAccessor;
  private final FileUtils fileUtils;
  private final AptoideDownloadManager downloadManager;
  private final CompositeSubscription subscriptions;
  private int currentDownload;
  private List<DownloadStatus> downloaders;
  private File apkPath;
  private File obbPath;
  private File genericPath;
  private FileDownloader fileDownloader;

  DownloadTask(int progressMaxValue, DownloadAccessor downloadAccessor, Download download,
      FileUtils fileUtils, Analytics analytics, AptoideDownloadManager downloadManager,
      CrashReport crashReport, File apkPath, File obbPath, File genericPath,
      FileDownloader fileDownloader) {
    this.progressMaxValue = progressMaxValue;
    this.download = download;
    this.downloadAccessor = downloadAccessor;
    this.fileUtils = fileUtils;
    this.downloadManager = downloadManager;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
    this.genericPath = genericPath;
    this.fileDownloader = fileDownloader;
    this.downloaders = new ArrayList<>();
    this.subscriptions = new CompositeSubscription();

    Map<String, String> headers = new HashMap<>();
    headers.put(Constants.VERSION_CODE, String.valueOf(download.getVersionCode()));
    headers.put(Constants.PACKAGE, download.getPackageName());
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
      headers.put(Constants.FILE_TYPE, String.valueOf(fileToDownload.getFileType()));

      DownloadTaskWrapper downloader = new DownloadTaskWrapper(fileDownloader, fileToDownload.getLink(),
          genericPath + fileToDownload.getFileName(), headers, RETRY_TIMES,
          fileToDownload.getFileName());
      fileToDownload.setDownloadId(downloader.getId());
      fileToDownload.setPath(downloader.getPath());
      fileToDownload.setFileName(downloader.fileName());

      saveDownloadInDb(download);

      subscriptions.add(downloader.getPending()
          .subscribe(downloadId -> setDownloadStatus(Download.PENDING, download, downloadId)));

      subscriptions.add(downloader.getProgress()
          .observeOn(Schedulers.io())
          .subscribe(downloadProgress -> {
            FileToDownload downloadingFile = download.getFilesToDownload()
                .get(currentDownload);
            //sometimes to totalBytes = 0, i believe that's when a 301(Moved Permanently) http error occurs
            if (downloadProgress.getTotalBytes() > 0) {
              downloadingFile.setProgress((int) Math.floor(
                  (float) downloadProgress.getSoFarBytes() / downloadProgress.getTotalBytes()
                      * this.progressMaxValue));
            } else {
              downloadingFile.setProgress(0);
            }
            this.download.setDownloadSpeed(downloadProgress.getSpeed() * 1024);
            updateProgress();
            setDownloadStatus(Download.PROGRESS, download, downloadProgress.getId());
          }, throwable -> crashReport.log(throwable)));

      subscriptions.add(downloader.getPause()
          .subscribe(downloadId -> {
            setDownloadStatus(Download.PAUSED, download, downloadId);
            onDownloadTermitaed();
            downloadManager.currentDownloadFinished();
          }));

      subscriptions.add(downloader.getComplete()
          .subscribe(downloadId -> {
            Observable.from(download.getFilesToDownload())
                .filter(file -> file.getDownloadId() == downloadId)
                .flatMapSingle(file -> {
                  file.setStatus(Download.COMPLETED);
                  for (final FileToDownload downloadingFile : download.getFilesToDownload()) {
                    if (downloadingFile.getStatus() != Download.COMPLETED) {
                      file.setProgress(this.progressMaxValue);
                      return Single.just(null);
                    }
                  }
                  return checkMd5AndMoveFileToRightPlace(download).doOnSuccess(fileMoved -> {
                    if (fileMoved) {
                      Logger.d(TAG, "Download md5 match");
                      file.setProgress(this.progressMaxValue);
                      updateProgress();
                    } else {
                      Logger.e(TAG, "Download md5 is not correct");
                      downloadManager.deleteDownloadlFiles(download);
                      download.setDownloadError(Download.GENERIC_ERROR);
                      setDownloadStatus(Download.ERROR, download, downloadId);
                    }
                  });
                })
                .doOnUnsubscribe(() -> downloadManager.setDownloading(false))
                .subscribeOn(Schedulers.io())
                .subscribe(success -> saveDownloadInDb(download),
                    throwable -> setDownloadStatus(Download.ERROR, download));
            download.setDownloadSpeed(0);
            startNextDownload();
          }));

      subscriptions.add(downloader.getError()
          .subscribe(downloadProgress -> {
            stopDownloadQueue(download);
            if (downloadProgress.getThrowable() instanceof FileDownloadHttpException
                && ((FileDownloadHttpException) downloadProgress.getThrowable()).getCode()
                == FILE_NOTFOUND_HTTP_ERROR) {
              for (final FileToDownload downloadingFile : download.getFilesToDownload()) {
                if (TextUtils.equals(downloadingFile.getLink(), downloadProgress.getUrl())
                    && !TextUtils.isEmpty(downloadingFile.getAltLink())) {
                  downloadingFile.setLink(downloadingFile.getAltLink());
                  downloadingFile.setAltLink(null);
                  downloadAccessor.save(download);
                  startDownload();
                  return;
                }
              }
            } else {
              Logger.d(TAG, "Error on download: " + download.getMd5());
              // Apparently throwable e can be null.
              if (downloadProgress.getThrowable() != null) {
                downloadProgress.getThrowable()
                    .printStackTrace();
              }
              if (analytics != null) {
                analytics.onError(download, downloadProgress.getThrowable());
              }
            }
            if (downloadProgress.getThrowable() instanceof FileDownloadOutOfSpaceException) {
              download.setDownloadError(Download.NOT_ENOUGH_SPACE_ERROR);
            } else {
              download.setDownloadError(Download.GENERIC_ERROR);
            }
            setDownloadStatus(Download.ERROR, download, downloadProgress.getId());
            onDownloadTermitaed();
            downloadManager.currentDownloadFinished();
          }));

      subscriptions.add(downloader.getWarn()
          .subscribe(downloadId -> setDownloadStatus(Download.WARN, download, downloadId)));

      downloaders.add(downloader);
    }
  }

  /**
   * Update the overall download progress. It updates the value on database and in memory list
   *
   * @return new current progress
   */
  @NonNull private Download updateProgress() {
    if (download.getOverallProgress() >= progressMaxValue
        || download.getOverallDownloadStatus() != Download.PROGRESS) {
      return download;
    }

    int progress = 0;
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      progress += fileToDownload.getProgress();
    }
    download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload()
        .size()));

    if (download.getOverallProgress() == progressMaxValue
        && download.getOverallDownloadStatus() != Download.COMPLETED) {
      setDownloadStatus(Download.COMPLETED, download);
      onDownloadTermitaed();
      downloadManager.currentDownloadFinished();
    }

    Logger.d(TAG, "Download: " + download.getMd5() + " Progress: " + download.getOverallProgress());
    return download;
  }

  private void setDownloadStatus(@Download.DownloadState int status, Download download) {
    setDownloadStatus(status, download, -1);
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
      long downloadId) {
    if (downloadId != -1) {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        if (fileToDownload.getDownloadId() == downloadId) {
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
  public boolean startDownload() throws IllegalArgumentException {
    return startNextDownload();
  }

  private boolean startNextDownload() {
    for (int i = 0; i < downloaders.size(); i++) {
      DownloadStatus downloader = downloaders.get(i);
      if (!downloader.isCompleted()) {
        if (downloader.startDownload()) {
          currentDownload = i;
          return true;
        } else {
          return false;
        }
      }
    }
    return false;
  }

  private Single<Boolean> checkMd5AndMoveFileToRightPlace(Download download) {
    return Single.fromCallable(() -> {
      for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
        final String fileName = fileToDownload.getFileName();
        fileToDownload.setFileName(fileName.replace(".temp", ""));
        final String downloadMd5 = fileToDownload.getMd5();
        if (!TextUtils.isEmpty(downloadMd5)) {
          final String computedMd5 =
              AptoideUtils.AlgorithmU.computeMd5(new File(genericPath, fileName));
          if (!TextUtils.equals(computedMd5, downloadMd5)) {
            return false;
          }
        }
        File newFilePath = getFilePathFromFileType(fileToDownload);
        fileUtils.copyFile(genericPath, newFilePath, fileName);
        fileToDownload.setPath(newFilePath.getAbsolutePath());
      }
      return true;
    });
  }

  @NonNull private File getFilePathFromFileType(FileToDownload fileToDownload) {
    File path;
    switch (fileToDownload.getFileType()) {
      case FileToDownload.APK:
        path = apkPath;
        break;
      case FileToDownload.OBB:
        path = new File(new File(obbPath, fileToDownload.getPackageName()), File.pathSeparator);
        break;
      case FileToDownload.GENERIC:
      default:
        path = genericPath;
        break;
    }
    return path;
  }

  private void onDownloadTermitaed() {
    if (!subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
  }
}
