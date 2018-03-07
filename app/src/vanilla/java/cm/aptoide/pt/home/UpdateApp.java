package cm.aptoide.pt.home;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class UpdateApp {

  private String name;
  private String packageName;
  private String version;
  private String icon;

  public UpdateApp(String name, String packageName, String version, String icon) {
    this.name = name;
    this.packageName = packageName;
    this.version = version;
    this.icon = icon;
  }
}
