package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;

/**
 * Created by filipegoncalves on 4/23/18.
 */

public class RewardApp extends Application {

  private double rewardValue;

  public RewardApp(String name, String icon, float rating, int downloads, String packageName,
      long appId, String tag, double rewardValue) {
    super(name, icon, rating, downloads, packageName, appId, tag);
    this.rewardValue = rewardValue;
  }

  public double getRewardValue() {
    return rewardValue;
  }
}
