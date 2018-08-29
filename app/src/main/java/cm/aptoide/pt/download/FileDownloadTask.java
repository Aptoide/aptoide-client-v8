package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadTask extends FileDownloadLargeFileListener {

  private PublishSubject<FileDownloadCallback> downloadStatus;

  public FileDownloadTask(PublishSubject<FileDownloadCallback> downloadStatus) {
    this.downloadStatus = downloadStatus;
  }

  @Override
  protected void pending(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.PENDING));
  }

  @Override
  protected void progress(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.PROGRESS));
  }

  @Override
  protected void paused(BaseDownloadTask baseDownloadTask, long soFarBytes, long totalBytes) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.PAUSED));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.COMPLETED));
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable error) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.ERROR));
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatus.onNext(new DownloadTaskStatus(DownloadTaskStatus.DownloadState.WARN));
  }

  public Observable<FileDownloadCallback> onDownloadStateChanged() {
    return downloadStatus;
  }
}
