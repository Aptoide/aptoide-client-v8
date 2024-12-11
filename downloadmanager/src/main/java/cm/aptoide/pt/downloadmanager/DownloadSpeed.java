package cm.aptoide.pt.downloadmanager;

public class DownloadSpeed {
  private long averageApkSpeed;
  private long averageObbSpeed;
  private long averageSplitSpeed;

  public DownloadSpeed(long averageApkSpeed, long averageObbSpeed, long averageSplitSpeed) {
    this.averageApkSpeed = averageApkSpeed;
    this.averageObbSpeed = averageObbSpeed;
    this.averageSplitSpeed = averageSplitSpeed;
  }

  public long getAverageApkSpeed() {
    return averageApkSpeed;
  }

  public long getAverageObbSpeed() {
    return averageObbSpeed;
  }

  public long getAverageSplitSpeed() {
    return averageSplitSpeed;
  }
}
