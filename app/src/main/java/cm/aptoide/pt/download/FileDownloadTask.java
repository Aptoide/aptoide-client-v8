package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.AppDownloadStatus;
import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import cm.aptoide.pt.downloadmanager.NewAptoideDownloadManager;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadTask extends FileDownloadLargeFileListener {

  private PublishSubject<FileDownloadCallback> downloadStatus;
  private int fileType;

  public FileDownloadTask(PublishSubject<FileDownloadCallback> downloadStatus, int fileType) {
    this.downloadStatus = downloadStatus;
    this.fileType = fileType;
  }

  @Override
  protected void pending(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.PENDING,
        calculateProgress(soFarBytes, totalBytes), fileType));
  }

  @Override
  protected void progress(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.PROGRESS,
        calculateProgress(soFarBytes, totalBytes), fileType));
  }

  @Override
  protected void paused(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.PAUSED,
        calculateProgress(soFarBytes, totalBytes), fileType));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.COMPLETED,
        NewAptoideDownloadManager.PROGRESS_MAX_VALUE, fileType));
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable error) {
    downloadStatus.onNext(
        new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, 0, fileType));
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(new DownloadTaskStatus(AppDownloadStatus.AppDownloadState.WARN));
  }

  public Observable<FileDownloadCallback> onDownloadStateChanged() {
    return downloadStatus;
  }

  private int calculateProgress(long soFarBytes, long totalBytes) {
    return (int) Math.floor(
        (float) soFarBytes / totalBytes * NewAptoideDownloadManager.PROGRESS_MAX_VALUE);
  }
}
