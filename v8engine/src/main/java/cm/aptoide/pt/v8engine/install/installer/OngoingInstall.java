package cm.aptoide.pt.v8engine.install.installer;

/**
 * Created by trinkes on 07/04/2017.
 */

public class OngoingInstall {
  private final String packageName;
  private final int versionCode;
  private final String versionName;
  private final int type;
  private final int status;

  public OngoingInstall(String packageName, int versionCode, String versionName, int type,
      int status) {

    this.packageName = packageName;
    this.versionCode = versionCode;
    this.versionName = versionName;

    this.type = type;
    this.status = status;
  }

  public int getType() {
    return type;
  }

  public int getStatus() {
    return status;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }
}
