package cm.aptoide.pt.app;

import rx.Single;

public class AppCoinsManager {

  private AppCoinsService appCoinsService;

  public AppCoinsManager(AppCoinsService appCoinsService) {
    this.appCoinsService = appCoinsService;
  }

  public Single<Boolean> hasAdvertising(String packageName, int versionCode) {
    return appCoinsService.isCampaignValid(packageName, versionCode);
  }
}
