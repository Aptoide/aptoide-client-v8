package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public class DownloadApp implements App {

  private String appName;
  private String md5;//to identify
  private String packageName;
  private String icon;
  private int progress;
  private boolean isIndeterminate;
  private int versionCode;
  private Status downloadStatus;

  public DownloadApp(String appName, String md5, String packageName, String icon, int progress,
      boolean isIndeterminate, int versionCode, Status downloadStatus) {
    this.appName = appName;
    this.md5 = md5;
    this.packageName = packageName;
    this.icon = icon;
    this.progress = progress;
    this.isIndeterminate = isIndeterminate;
    this.versionCode = versionCode;
    this.downloadStatus = downloadStatus;
  }

  public String getAppName() {
    return appName;
  }

  public String getMd5() {
    return md5;
  }

  public String getIcon() {
    return icon;
  }

  public int getProgress() {
    return progress;
  }

  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public Status getDownloadStatus() {
    return downloadStatus;
  }

  @Override public Type getType() {
    return Type.DOWNLOAD;
  }

  public String getPackageName() {
    return packageName;
  }

  public enum Status {
    ACTIVE, STANDBY, COMPLETED, ERROR;
  }
}
