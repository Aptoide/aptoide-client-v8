package cm.aptoide.pt.downloadmanager;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by trinkes on 05/06/2017.
 */

public class DownloadProgress {
  final long id;
  final BaseDownloadTask task;
  final Throwable throwable;
  final String url;
  final int speed;
  final private long soFarBytes;
  final private long totalBytes;

  public DownloadProgress(int id, int speed, long soFarBytes, long totalBytes,
      BaseDownloadTask task, Throwable throwable, String url) {
    this.id = id;
    this.speed = speed;
    this.soFarBytes = soFarBytes;
    this.totalBytes = totalBytes;
    this.task = task;
    this.throwable = throwable;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public BaseDownloadTask getTask() {
    return task;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public long getSoFarBytes() {
    return soFarBytes;
  }

  public long getTotalBytes() {
    return totalBytes;
  }

  public long getId() {
    return id;
  }

  public int getSpeed() {
    return speed;
  }
}
