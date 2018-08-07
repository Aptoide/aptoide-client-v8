package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.Constants;
import cm.aptoide.pt.downloadmanager.DownloadAppFile;
import cm.aptoide.pt.downloadmanager.FileDownloader;
import com.liulishuo.filedownloader.BaseDownloadTask;
import rx.Completable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class FileDownloadManager implements FileDownloader {

  public static final int RETRY_TIMES = 3;
  private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
  private static final int PROGRESS_MAX_VALUE = 100;

  private com.liulishuo.filedownloader.FileDownloader fileDownloader;
  private FileDownloadTask fileDownloadTask;
  private String downloadsPath;
  private int downloadId;

  public FileDownloadManager(com.liulishuo.filedownloader.FileDownloader fileDownloader,
      FileDownloadTask fileDownloadTask, String downloadsPath) {
    this.fileDownloader = fileDownloader;
    this.fileDownloadTask = fileDownloadTask;
    this.downloadsPath = downloadsPath;
  }

  @Override public Completable startFileDownload(DownloadAppFile downloadAppFile) {
    return Completable.fromCallable(() -> {
      if (downloadAppFile.getMainDownloadPath() == null || downloadAppFile.getMainDownloadPath()
          .isEmpty()) {
        throw new IllegalArgumentException("The url for the download can not be empty");
      } else {
        createBaseDownloadTask(downloadAppFile);
        return fileDownloader.start(fileDownloadTask, false);
      }
    });
  }

  @Override public Completable pauseDownload(DownloadAppFile downloadAppFile) {
    return Completable.fromAction(() -> {
      fileDownloader.pause(fileDownloadTask);
    });
  }

  @Override public Completable removeDownloadFile(DownloadAppFile downloadAppFile) {
    return Completable.fromAction(
        () -> fileDownloader.clear(downloadId, downloadAppFile.getMainDownloadPath()));
  }

  private void createBaseDownloadTask(DownloadAppFile downloadAppFile) {

    BaseDownloadTask baseDownloadTask =
        fileDownloader.create(downloadAppFile.getMainDownloadPath());
    baseDownloadTask.setAutoRetryTimes(RETRY_TIMES);

    baseDownloadTask.addHeader(Constants.VERSION_CODE,
        String.valueOf(downloadAppFile.getVersionCode()));
    baseDownloadTask.addHeader(Constants.PACKAGE, downloadAppFile.getPackageName());
    baseDownloadTask.addHeader(Constants.FILE_TYPE, String.valueOf(downloadAppFile.getFileType()));
    baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, fileDownloadTask);
    baseDownloadTask.setListener(fileDownloadTask);
    baseDownloadTask.setCallbackProgressTimes(PROGRESS_MAX_VALUE);
    baseDownloadTask.setPath(downloadsPath + downloadAppFile.getFileName());
    this.downloadId = baseDownloadTask.asInQueueTask()
        .enqueue();
  }
}
