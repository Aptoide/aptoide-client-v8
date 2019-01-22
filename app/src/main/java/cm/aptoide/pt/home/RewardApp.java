package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;

class RewardApp extends Application {
  RewardApp(String appName, String appIcon, float ratingAverage, int downloadsNumber,
      String packageName, long appId, String tag, boolean hasBilling, boolean hasAdvertising) {
    super(appName, appIcon, ratingAverage, downloadsNumber, packageName, appId, tag, hasBilling);
  }
}
