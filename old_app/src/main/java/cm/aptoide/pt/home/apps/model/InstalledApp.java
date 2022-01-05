package cm.aptoide.pt.home.apps.model;

import cm.aptoide.pt.home.apps.App;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class InstalledApp implements App {

  private final String appName;
  private final String packageName;
  private final String version;
  private final String icon;

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

  @Override public String getIdentifier() {
    return packageName;
  }
}
