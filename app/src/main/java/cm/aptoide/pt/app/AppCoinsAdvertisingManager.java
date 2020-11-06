package cm.aptoide.pt.app;

import rx.Single;

public class AppCoinsAdvertisingManager {

  private final AppCoinsService appCoinsService;

  public AppCoinsAdvertisingManager(AppCoinsService appCoinsService) {
    this.appCoinsService = appCoinsService;
  }

  public Single<AppCoinsAdvertisingModel> getAdvertising(String packageName, int versionCode) {
    return appCoinsService.getValidCampaign(packageName, versionCode);
  }
}
