package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class InstalledApp {

  private String name;
  private String packageName;
  private String version;
  private String icon;

  public InstalledApp(String name, String packageName, String version, String icon) {
    this.name = name;
    this.packageName = packageName;
    this.version = version;
    this.icon = icon;
  }

  public String getName() {
    return name;
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
}
