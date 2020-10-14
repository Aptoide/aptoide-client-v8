package cm.aptoide.pt.app;

import cm.aptoide.pt.app.appc.BonusAppcModel;
import cm.aptoide.pt.app.appc.BonusAppcService;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.app.view.donations.DonationsService;
import cm.aptoide.pt.logger.Logger;
import java.util.List;
import rx.Single;

public class AppCoinsManager {

  private final AppCoinsService appCoinsService;
  private final DonationsService donationsService;
  private final BonusAppcService bonusAppcService;
  private BonusAppcModel cachedBonusAppcModel;

  public AppCoinsManager(AppCoinsService appCoinsService, DonationsService donationsService,
      BonusAppcService bonusAppcService) {
    this.appCoinsService = appCoinsService;
    this.donationsService = donationsService;
    this.bonusAppcService = bonusAppcService;
  }

  public Single<BonusAppcModel> getBonusAppc() {
    if (cachedBonusAppcModel != null) {
      return Single.just(cachedBonusAppcModel);
    } else {
      return bonusAppcService.getBonusAppc()
          .doOnSuccess(bonusAppcModel -> cachedBonusAppcModel = bonusAppcModel);
    }
  }

  public Single<AppCoinsAdvertisingModel> getAdvertising(String packageName, int versionCode) {
    return appCoinsService.getValidCampaign(packageName, versionCode);
  }

  public Single<List<Donation>> getDonationsList(String packageName) {
    return donationsService.getDonations(packageName);
  }
}
