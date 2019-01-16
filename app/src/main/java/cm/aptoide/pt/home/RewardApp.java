package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;

public class RewardApp extends Application {
  private String clickUrl;
  private String downloadUrl;
  private String appId;

  public RewardApp(String appName, String appIcon, float ratingAverage, int downloadsNumber,
      String packageName, String appId, String tag, boolean hasBilling, boolean hasAdvertising,
      String clickUrl, String downloadUrl) {
    super(appName, appIcon, ratingAverage, downloadsNumber, packageName, tag, hasBilling,
        hasAdvertising);
    this.clickUrl = clickUrl;
    this.downloadUrl = downloadUrl;
    this.appId = appId;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public String getAppId() {
    return appId;
  }
}
