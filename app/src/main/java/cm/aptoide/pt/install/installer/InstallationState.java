package cm.aptoide.pt.install.installer;

/**
 * Created by trinkes on 10/04/2017.
 */

public class InstallationState {
  private final String packageName;
  private final int versionCode;
  private final String versionName;
  private final int status;
  private final int type;
  private final String name;
  private final String icon;
  private final long appSize;

  public InstallationState(String packageName, int versionCode, int status, int type, long appSize) {
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.status = status;
    this.type = type;
    this.appSize = appSize;
    name = null;
    icon = null;
    versionName = "";
  }

  public InstallationState(String packageName, int versionCode, String versionName, int status,
      int type, String name, String icon, long appSize) {
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.status = status;
    this.type = type;
    this.name = name;
    this.icon = icon;
    this.appSize = appSize;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public int getStatus() {
    return status;
  }

  public int getType() {
    return type;
  }

  public String getVersionName() {
    return versionName;
  }

  public long getAppSize() {
    return appSize;
  }
}
