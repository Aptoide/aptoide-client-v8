package cm.aptoide.pt.promotions;

public class PromotionApp {

  private String name;
  private String packageName;
  private long appId;
  private String downloadPath;
  private String alternativePath;
  private String appIcon;
  private PromotionAppState state;

  public PromotionApp(String name, String packageName, long appId, String downloadPath,
      String alternativePath, String appIcon, PromotionAppState state) {
    this.name = name;
    this.packageName = packageName;
    this.appId = appId;
    this.downloadPath = downloadPath;
    this.alternativePath = alternativePath;
    this.appIcon = appIcon;
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public String getAlternativePath() {
    return alternativePath;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public PromotionAppState getState() {
    return state;
  }

  public void setState(PromotionAppState state) {
    this.state = state;
  }

  enum PromotionAppState {
    DOWNLOAD, UPDATE, DOWNLOADING, INSTALL, CLAIM, CLAIMED
  }
}
