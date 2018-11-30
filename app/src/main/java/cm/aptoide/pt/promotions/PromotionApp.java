package cm.aptoide.pt.promotions;

public class PromotionApp {

  private String name;
  private String packageName;
  private long appId;
  private String downloadPath;
  private String alternativePath;

  public PromotionApp(String name, String packageName, long appId, String downloadPath,
      String alternativePath) {
    this.name = name;
    this.packageName = packageName;
    this.appId = appId;
    this.downloadPath = downloadPath;
    this.alternativePath = alternativePath;
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
}
