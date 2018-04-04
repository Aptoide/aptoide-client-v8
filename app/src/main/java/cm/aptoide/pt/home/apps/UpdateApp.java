package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class UpdateApp implements App {

  private String name;
  private String md5;
  private String icon;
  private String packageName;
  private int progress;
  private boolean isIndeterminate;
  private String version;
  private int versionCode;
  private UpdateStatus updateStatus;
  private long appId;

  public UpdateApp(String name, String md5, String icon, String packageName, int progress,
      boolean isIndeterminate, String version, int versionCode, UpdateStatus updateStatus,
      long appId) {
    this.name = name;
    this.md5 = md5;
    this.icon = icon;
    this.packageName = packageName;
    this.progress = progress;
    this.isIndeterminate = isIndeterminate;
    this.version = version;
    this.versionCode = versionCode;
    this.updateStatus = updateStatus;
    this.appId = appId;
  }

  @Override public Type getType() {
    return Type.UPDATE;
  }

  public String getName() {
    return name;
  }

  public String getMd5() {
    return md5;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getVersion() {
    return version;
  }

  public String getIcon() {
    return icon;
  }

  public UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  public int getProgress() {
    return progress;
  }

  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof UpdateApp)) {
      return false;
    }
    UpdateApp other = ((UpdateApp) obj);
    return md5.equals(other.getMd5()) && getType().equals(other.getType());
  }

  public int getVersionCode() {
    return versionCode;
  }

  public long getAppId() {
    return appId;
  }

  public enum UpdateStatus {
    UPDATE, STANDBY, UPDATING, ERROR
  }
}
