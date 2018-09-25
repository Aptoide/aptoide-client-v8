package cm.aptoide.pt.download;

import android.util.Log;
import cm.aptoide.pt.downloadmanager.AppDownloadStatus;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadTask extends FileDownloadLargeFileListener {

  private final String md5;
  private PublishSubject<FileDownloadCallback> downloadStatus;
  private int fileType;
  private Md5Comparator md5Comparator;
  private String fileName;

  public FileDownloadTask(PublishSubject<FileDownloadCallback> downloadStatus, int fileType,
      String md5, Md5Comparator md5Comparator, String fileName) {
    this.downloadStatus = downloadStatus;
    this.fileType = fileType;
    this.md5 = md5;
    this.md5Comparator = md5Comparator;
    this.fileName = fileName;
  }

  @Override
  protected void pending(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PENDING,
        calculateProgress(soFarBytes, totalBytes), fileType, md5));
  }

  @Override
  protected void progress(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PROGRESS,
        calculateProgress(soFarBytes, totalBytes), fileType, md5));
  }

  @Override
  protected void paused(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.PAUSED,
        calculateProgress(soFarBytes, totalBytes), fileType, md5));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    if (md5Comparator.compareMd5(md5, fileName)) {
      downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.COMPLETED,
          FileDownloadManager.PROGRESS_MAX_VALUE, fileType, md5));
      Log.d("FileDownloader", " Download completed");
    } else {
      Log.d("FileDownloader", " Download error");
      downloadStatus.onNext(
          new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, 0, fileType, md5));
    }
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable error) {
    downloadStatus.onNext(
        new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, 0, fileType, md5));
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(new FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.WARN, md5));
  }

  public Observable<FileDownloadCallback> onDownloadStateChanged() {
    return downloadStatus;
  }

  private int calculateProgress(long soFarBytes, long totalBytes) {
    return (int) Math.floor(
        (float) soFarBytes / totalBytes * FileDownloadManager.PROGRESS_MAX_VALUE);
  }
}
