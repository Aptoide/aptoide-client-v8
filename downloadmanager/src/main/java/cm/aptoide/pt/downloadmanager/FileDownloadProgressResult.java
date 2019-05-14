package cm.aptoide.pt.downloadmanager;

public class FileDownloadProgressResult {

  private long downloadedBytes;
  private long totalFileBytes;

  public FileDownloadProgressResult(long downloadedBytes, long totalFileBytes) {
    this.downloadedBytes = downloadedBytes;
    this.totalFileBytes = totalFileBytes;
  }

  public long getDownloadedBytes() {
    return downloadedBytes;
  }

  public long getTotalFileBytes() {
    return totalFileBytes;
  }
}
