package cm.aptoide.pt.downloadmanager;

import android.support.annotation.CheckResult;
import android.text.TextUtils;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import java.io.File;
import java.util.Map;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 02/06/2017.
 */
class DownloadTaskWrapper extends FileDownloadLargeFileListener implements DownloadStatus {
  private final FileDownloader fileDownloader;
  private final BaseDownloadTask baseDownloadTask;
  private PublishSubject<DownloadProgress> progress;
  private BehaviorRelay<Integer> pending;
  private BehaviorRelay<Integer> pause;
  private BehaviorRelay<Integer> complete;
  private BehaviorRelay<DownloadProgress> error;
  private BehaviorRelay<Integer> warn;
  private boolean completed;

  /**
   * @throws IllegalArgumentException if the provided path or url is not valid
   */
  DownloadTaskWrapper(FileDownloader fileDownloader, String url, File path,
      Map<String, String> headers, int retryTimes, String fileName) {
    this.fileDownloader = fileDownloader;
    progress = PublishSubject.create();
    pending = BehaviorRelay.create();
    pause = BehaviorRelay.create();
    complete = BehaviorRelay.create();
    error = BehaviorRelay.create();
    warn = BehaviorRelay.create();
    validateArguments(url, path);

    baseDownloadTask = fileDownloader.create(url)
        .setAutoRetryTimes(retryTimes)
        .setListener(this)
        .setPath(path + fileName);
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      baseDownloadTask.addHeader(entry.getKey(), entry.getValue());
    }
    baseDownloadTask.asInQueueTask()
        .enqueue();
    completed = false;
  }

  private void validateArguments(String url, File path) throws IllegalArgumentException {
    if (TextUtils.isEmpty(url)) {
      throw new IllegalArgumentException("An url should be provided. (url = " + url + ")");
    }
    if (path == null || TextUtils.isEmpty(path.getAbsolutePath())) {
      throw new IllegalArgumentException(
          "A valid path should be provided. (path = " + path + ")");
    }
  }

  @Override protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    pending.call(task.getId());
  }

  @Override protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    DownloadProgress downloadProgress =
        new DownloadProgress(task.getId(), task.getSpeed(), soFarBytes, totalBytes, task, null,
            task.getUrl());
    progress.onNext(downloadProgress);
  }

  @Override protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    pause.call(task.getId());
  }

  @Override protected void completed(BaseDownloadTask task) {
    completed = true;
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

  public Observable<Integer> getPending() {
    return pending;
  }

  public Observable<DownloadProgress> getProgress() {
    return progress.onBackpressureDrop();
  }

  public Observable<Integer> getPause() {
    return pause;
  }

  public Observable<Integer> getComplete() {
    return complete;
  }

  public Observable<DownloadProgress> getError() {
    return error;
  }

  public Observable<Integer> getWarn() {
    return warn;
  }

  public int getId() {
    return baseDownloadTask.getId();
  }

  public String getPath() {
    return baseDownloadTask.getPath();
  }

  public String fileName() {
    return baseDownloadTask.getFilename();
  }

  @Override public boolean isCompleted() {
    return completed;
  }

  @CheckResult @Override public boolean startDownload() {
    return fileDownloader.start(this, true);
  }
}
