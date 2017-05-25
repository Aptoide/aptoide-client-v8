package cm.aptoide.pt.downloadmanager;

import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import io.realm.RealmList;

/**
 * Created by trinkes on 25/05/2017.
 */

public class DownloadT extends FileDownloadLargeFileListener {

  private static final int RETRY_TIMES = 3;
  private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
  private static final int FILE_NOTFOUND_HTTP_ERROR = 404;
  private static final String TAG = DownloadT.class.getSimpleName();
  private final DownloadSaver downloadSaver;
  private final Download download;
  private final DownloadValidator downloadValidator;
  private FileDownloader fileDownloader;
  private CrashReport crashReport;

  public DownloadT(Download download, FileDownloader fileDownloader, String basePath,
      DownloadSaver downloadSaver, CrashReport crashReport, DownloadValidator downloadValidator) {
    this.download = download;
    this.fileDownloader = fileDownloader;
    this.downloadSaver = downloadSaver;
    this.crashReport = crashReport;
    this.downloadValidator = downloadValidator;

    RealmList<FileToDownload> filesToDownload = download.getFilesToDownload();
    if (download.getFilesToDownload() != null) {

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
            .setPath(basePath + fileToDownload.getFileName())
            .asInQueueTask()
            .enqueue());
        fileToDownload.setPath(basePath);
        fileToDownload.setFileName(fileToDownload.getFileName() + ".temp");
      }
    }
  }

  public void startDownload() {
    fileDownloader.start(this, true);
  }

  public void stop() {
    fileDownloader.pause(this);
  }

  @Override protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    saveDownload(Download.IN_QUEUE);
  }

  @Override protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    updateDownloadProgress(task.getId(),
        (int) Math.floor(soFarBytes / totalBytes * AptoideDownloadManager.PROGRESS_MAX_VALUE));
    updateOverallProgress();
    this.download.setDownloadSpeed(task.getSpeed() * 1024);
    saveDownload(Download.PROGRESS);
  }

  @Override protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    saveDownload(Download.PAUSED);
  }

  @Override protected void completed(BaseDownloadTask task) {
    currentFileFinished(task.getId());

    if (downloadValidator.isFinish(download)) {
      if (downloadValidator.isMd5Valid(download)) {
        saveDownload(Download.COMPLETED);
      } else {
        // TODO: 25/05/2017 trinkes handle md5 not match error
        download.setDownloadError(Download.GENERIC_ERROR);
        saveDownload(Download.ERROR);
      }
    }
  }

  @Override protected void error(BaseDownloadTask task, Throwable e) {
    if (e instanceof FileDownloadHttpException
        && ((FileDownloadHttpException) e).getCode() == FILE_NOTFOUND_HTTP_ERROR) {
      download.setDownloadError(Download.FILE_NOT_FOUND);
    } else if (e instanceof FileDownloadOutOfSpaceException) {
      download.setDownloadError(Download.NOT_ENOUGH_SPACE_ERROR);
    } else {
      download.setDownloadError(Download.GENERIC_ERROR);
      crashReport.log(e);
    }
    saveDownload(Download.ERROR);
  }

  @Override protected void warn(BaseDownloadTask task) {
    saveDownload(Download.IN_QUEUE);
  }

  private void currentFileFinished(int downloadId) {
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (fileToDownload.getDownloadId() == downloadId) {
        fileToDownload.setStatus(Download.COMPLETED);
      }
    }
  }

  private void updateDownloadProgress(int downloadId, int progress) {
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (fileToDownload.getDownloadId() == downloadId) {
        //sometimes to totalBytes = 0, i believe that's when a 301(Moved Permanently) http error occurs
        if (progress > 0) {
          fileToDownload.setProgress(progress);
        } else {
          fileToDownload.setProgress(0);
        }
      }
    }
  }

  private void updateOverallProgress() {
    int progress = 0;
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      progress += fileToDownload.getProgress();
    }
    download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload()
        .size()));
    Logger.d(TAG, "Download: " + download.getMd5() + " Progress: " + download.getOverallProgress());
  }

  private void saveDownload(@Download.DownloadState int status) {
    download.setOverallDownloadStatus(status);
    downloadSaver.save(download);
  }
}
