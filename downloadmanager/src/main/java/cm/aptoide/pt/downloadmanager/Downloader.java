package cm.aptoide.pt.downloadmanager;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;

/**
 * Created by trinkes on 02/06/2017.
 */

public class Downloader extends FileDownloadLargeFileListener implements DownloadStatus {
  private BehaviorRelay<Integer> pending;
  private BehaviorRelay<DownloadProgress> progress;
  private BehaviorRelay<Integer> pause;
  private BehaviorRelay<Integer> complete;
  private BehaviorRelay<DownloadProgress> error;
  private BehaviorRelay<Integer> warn;

  /**
   * @throws IllegalArgumentException if the provided path or url is not valid
   */
  public Downloader() {
    pending = BehaviorRelay.create();
    progress = BehaviorRelay.create();
    pause = BehaviorRelay.create();
    complete = BehaviorRelay.create();
    error = BehaviorRelay.create();
    warn = BehaviorRelay.create();
  }

  public BehaviorRelay<Integer> getPending() {
    return pending;
  }

  public BehaviorRelay<DownloadProgress> getProgress() {
    return progress;
  }

  public BehaviorRelay<Integer> getPause() {
    return pause;
  }

  public BehaviorRelay<Integer> getComplete() {
    return complete;
  }

  public BehaviorRelay<DownloadProgress> getError() {
    return error;
  }

  public BehaviorRelay<Integer> getWarn() {
    return warn;
  }

  @Override protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    pending.call(task.getId());
  }

  @Override protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    DownloadProgress downloadProgress =
        new DownloadProgress(task.getId(), task.getSpeed(), soFarBytes, totalBytes, task, null,
            task.getUrl());
    progress.call(downloadProgress);
  }

  @Override protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    pause.call(task.getId());
  }

  @Override protected void completed(BaseDownloadTask task) {
    complete.call(task.getId());
  }

  @Override protected void error(BaseDownloadTask task, Throwable e) {
    DownloadProgress downloadProgress =
        new DownloadProgress(task.getId(), task.getSpeed(), 0, 0, task, e, task.getUrl());
    error.call(downloadProgress);
  }

  @Override protected void warn(BaseDownloadTask task) {
    warn.call(task.getId());
  }
}
