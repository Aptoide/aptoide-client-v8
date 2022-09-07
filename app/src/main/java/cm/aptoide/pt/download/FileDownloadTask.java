package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import cm.aptoide.pt.downloadmanager.FileDownloadProgressResult;
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
  private final String attributionId;
  private final PublishSubject<FileDownloadCallback> downloadStatus;
  private final Md5Comparator md5Comparator;
  private final String fileName;
  private final boolean shouldConfirmMd5;

  public FileDownloadTask(PublishSubject<FileDownloadCallback> downloadStatus, String md5,
      Md5Comparator md5Comparator, String fileName, String attributionId,
      boolean shouldConfirmMd5) {
    this.downloadStatus = downloadStatus;
    this.md5 = md5;
    this.md5Comparator = md5Comparator;
    this.fileName = fileName;
    this.attributionId = attributionId;
    this.shouldConfirmMd5 = shouldConfirmMd5;
  }

  @Override
  protected void pending(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PENDING,
        new FileDownloadProgressResult(soFarBytes, totalBytes), md5));
  }

  @Override
  protected void progress(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PROGRESS,
        new FileDownloadProgressResult(soFarBytes, totalBytes), md5));
  }

  @Override
  protected void paused(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PAUSED,
        new FileDownloadProgressResult(soFarBytes, totalBytes), md5));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    new Thread(() -> {
      FileDownloadTaskStatus fileDownloadTaskStatus1 =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.VERIFYING_FILE_INTEGRITY,
              new FileDownloadProgressResult(baseDownloadTask.getLargeFileTotalBytes(),
                  baseDownloadTask.getLargeFileTotalBytes()), md5);
      downloadStatus.onNext(fileDownloadTaskStatus1);

      FileDownloadTaskStatus fileDownloadTaskStatus;
      if (attributionId != null || isMd5Approved(md5, fileName, shouldConfirmMd5)) {
        fileDownloadTaskStatus =
            new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.COMPLETED,
                new FileDownloadProgressResult(baseDownloadTask.getLargeFileTotalBytes(),
                    baseDownloadTask.getLargeFileTotalBytes()), md5);
        Logger.getInstance()
            .d(TAG, " Download completed");
      } else {
        Logger.getInstance()
            .d(TAG, " Download error in md5");
        fileDownloadTaskStatus =
            new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR_MD5_DOES_NOT_MATCH,
                md5, new Md5DownloadComparisonException("md5 does not match"));
      }
      downloadStatus.onNext(fileDownloadTaskStatus);
    }).start();
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable error) {
    FileDownloadTaskStatus fileDownloadTaskStatus;

    if (error != null) {
      error.printStackTrace();
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
            new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE,
                md5, error);
      } else {
        Logger.getInstance()
            .d(TAG, "Generic error on app: " + md5);
        fileDownloadTaskStatus =
            new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, md5, error);
      }
    } else {
      Logger.getInstance()
          .d(TAG, "Unknown error on app: " + md5);
      fileDownloadTaskStatus =
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, md5,
              new GeneralDownloadErrorException("Empty download error"));
    }
    downloadStatus.onNext(fileDownloadTaskStatus);
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(
        new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.WARN, md5, null));
  }

  private boolean isMd5Approved(String md5, String fileName, boolean shouldConfirmMd5) {
    if (shouldConfirmMd5) {
      return md5Comparator.compareMd5(md5, fileName);
    } else {
      return true;
    }
  }

  public Observable<FileDownloadCallback> onDownloadStateChanged() {
    return downloadStatus;
  }
}
