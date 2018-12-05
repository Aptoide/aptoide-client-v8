package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import cm.aptoide.pt.logger.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadTask extends FileDownloadLargeFileListener {

  private static final int FILE_NOT_FOUND_HTTP_ERROR = 404;
  private final String TAG = "FileDownloader";
  private final String md5;
  private PublishSubject<FileDownloadCallback> downloadStatus;
  private Md5Comparator md5Comparator;
  private String fileName;

  public FileDownloadTask(PublishSubject<FileDownloadCallback> downloadStatus, String md5,
      Md5Comparator md5Comparator, String fileName) {
    this.downloadStatus = downloadStatus;
    this.md5 = md5;
    this.md5Comparator = md5Comparator;
    this.fileName = fileName;
  }

  @Override
  protected void pending(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PENDING,
        calculateProgress(soFarBytes, totalBytes), md5));
  }

  @Override
  protected void progress(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PROGRESS,
        calculateProgress(soFarBytes, totalBytes), md5));
  }

  @Override
  protected void paused(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PAUSED,
        calculateProgress(soFarBytes, totalBytes), md5));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    FileDownloadTaskStatus fileDownloadTaskStatus;
    if (md5Comparator.compareMd5(md5, fileName)) {
      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.COMPLETED,
              FileDownloadManager.PROGRESS_MAX_VALUE, md5);
      Logger.getInstance()
          .d(TAG, " Download completed");
    } else {
      Logger.getInstance()
          .d(TAG, " Download error in md5");
      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, md5,
              new Md5DownloadComparisionException("md5 does not match"));
    }
    downloadStatus.onNext(fileDownloadTaskStatus);
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable error) {
    error.printStackTrace();
    FileDownloadTaskStatus fileDownloadTaskStatus;
    if (error instanceof FileDownloadHttpException
        && ((FileDownloadHttpException) error).getCode() == FILE_NOT_FOUND_HTTP_ERROR) {
      Logger.getInstance()
          .d(TAG, "File not found error on app: " + md5);
      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND, md5,
              error);
    } else if (error instanceof FileDownloadOutOfSpaceException) {
      Logger.getInstance()
          .d(TAG, "Out of space error for the app: " + md5);

      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE, md5,
              error);
    } else {
      Logger.getInstance()
          .d(TAG, "Generic error on app: " + md5);
      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, md5, error);
    }
    downloadStatus.onNext(fileDownloadTaskStatus);
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(
        new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.WARN, md5, null));
  }

  public Observable<FileDownloadCallback> onDownloadStateChanged() {
    return downloadStatus;
  }

  private int calculateProgress(long soFarBytes, long totalBytes) {
    return (int) Math.floor(
        (float) soFarBytes / totalBytes * FileDownloadManager.PROGRESS_MAX_VALUE);
  }
}
