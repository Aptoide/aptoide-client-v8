package cm.aptoide.pt.view.app;

public abstract class Application {
  private final String name;
  private final String icon;
  private final float rating;
  private final int downloads;
  private final String packageName;
  private final String tag;
  private final boolean hasBilling;
  private final boolean hasAdvertising;

  public Application(String name, String icon, float rating, int downloads, String packageName,
      String tag, boolean hasBilling, boolean hasAdvertising) {
    this.name = name;
    this.icon = icon;
    this.rating = rating;
    this.downloads = downloads;
    this.packageName = packageName;
    this.tag = tag;
    this.hasBilling = hasBilling;
    this.hasAdvertising = hasAdvertising;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getIcon() {
    return icon;
  }

  public float getRating() {
    return rating;
  }

  public int getDownloads() {
    return downloads;
  }

  public String getName() {
    return name;
  }

  public String getTag() {
    return tag;
  }

  public boolean hasAppcBilling() {
    return hasBilling;
  }

  public boolean hasAppcAdvertising() {
    return hasAdvertising;
  }
}
