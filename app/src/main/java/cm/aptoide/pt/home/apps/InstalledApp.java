package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class InstalledApp implements App {

  private String appName;
  private String packageName;
  private String version;
  private String icon;

  public InstalledApp(String appName, String packageName, String version, String icon) {
    this.appName = appName;
    this.packageName = packageName;
    this.version = version;
    this.icon = icon;
  }

  public String getAppName() {
    return appName;
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

  @Override public Type getType() {
    return Type.INSTALLED;
  }
}
