package cm.aptoide.pt.view.app;

/**
 * Created by trinkes on 18/10/2017.
 */

public class Application {
  private final String name;
  private final String icon;
  private final float avg;
  private final int downloads;
  private final long appId;
  private final String packageName;

  public Application(String name, String icon, float avg, int downloads, String packageName,
      long appId) {
    this.name = name;
    this.icon = icon;
    this.avg = avg;
    this.downloads = downloads;
    this.appId = appId;
    this.packageName = packageName;
  }

  public long getAppId() {
    return appId;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getIcon() {
    return icon;
  }

  public float getAvg() {
    return avg;
  }

  public int getDownloads() {
    return downloads;
  }

  public String getName() {
    return name;
  }
}
