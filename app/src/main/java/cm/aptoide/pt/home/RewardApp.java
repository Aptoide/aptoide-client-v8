package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;

public class RewardApp extends Application {
  private String clickUrl;
  private String downloadUrl;
  private float reward;

  public RewardApp(String appName, String appIcon, float ratingAverage, int downloadsNumber,
      String packageName, long appId, String tag, boolean hasBilling, String clickUrl,
      String downloadUrl, float reward) {
    super(appName, appIcon, ratingAverage, downloadsNumber, packageName, appId, tag, hasBilling);
    this.clickUrl = clickUrl;
    this.downloadUrl = downloadUrl;
    this.reward = reward;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public float getReward() {
    return reward;
  }
}
