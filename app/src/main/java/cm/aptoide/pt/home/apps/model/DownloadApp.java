package cm.aptoide.pt.home.apps.model;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public class DownloadApp implements StateApp {

  private String appName;
  private String md5;//to identify
  private String packageName;
  private String icon;
  private int progress;
  private boolean isIndeterminate;
  private int versionCode;
  private StateApp.Status downloadStatus;

  public DownloadApp(String appName, String md5, String packageName, String icon, int progress,
      boolean isIndeterminate, int versionCode, StateApp.Status downloadStatus) {
    this.appName = appName;
    this.md5 = md5;
    this.packageName = packageName;
    this.icon = icon;
    this.progress = progress;
    this.isIndeterminate = isIndeterminate;
    this.versionCode = versionCode;
    this.downloadStatus = downloadStatus;
  }

  public String getName() {
    return appName;
  }

  public String getMd5() {
    return md5;
  }

  public String getIcon() {
    return icon;
  }

  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  @Override public void setIndeterminate(boolean indeterminate) {
    this.isIndeterminate = indeterminate;
  }

  @Override public StateApp.Status getStatus() {
    return downloadStatus;
  }

  @Override public void setStatus(Status status) {
    this.downloadStatus = status;
  }

  @Override public int getProgress() {
    return progress;
  }

  public int getVersionCode() {
    return versionCode;
  }

  @Override public Type getType() {
    return Type.DOWNLOAD;
  }

  @Override public String getIdentifier() {
    return md5;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof DownloadApp)) {
      return false;
    }
    DownloadApp other = ((DownloadApp) obj);
    return md5.equals(other.getMd5()) && getType().equals(other.getType());
  }

  @Override public String toString() {
    return "DownloadApp{"
        + "appName='"
        + appName
        + '\''
        + ", md5='"
        + md5
        + '\''
        + ", packageName='"
        + packageName
        + '\''
        + ", icon='"
        + icon
        + '\''
        + ", progress="
        + progress
        + ", isIndeterminate="
        + isIndeterminate
        + ", versionCode="
        + versionCode
        + ", downloadStatus="
        + downloadStatus
        + '}';
  }
}
