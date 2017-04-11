package cm.aptoide.pt.v8engine.install.installer;

/**
 * Created by trinkes on 10/04/2017.
 */

public class InstallationState {
  String packageName;
  int versionCode;
  int status;
  int type;

  public InstallationState(String packageName, int versionCode, int status, int type) {
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.status = status;
    this.type = type;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
