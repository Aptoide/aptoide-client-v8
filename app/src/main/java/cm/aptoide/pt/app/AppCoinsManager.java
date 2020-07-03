package cm.aptoide.pt.app;

import cm.aptoide.pt.app.appc.BonusAppcModel;
import cm.aptoide.pt.app.appc.BonusAppcService;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.app.view.donations.DonationsService;
import java.util.List;
import rx.Single;

public class AppCoinsManager {

  private AppCoinsService appCoinsService;
  private DonationsService donationsService;
  private BonusAppcService bonusAppcService;

  public AppCoinsManager(AppCoinsService appCoinsService, DonationsService donationsService,
      BonusAppcService bonusAppcService) {
    this.appCoinsService = appCoinsService;
    this.donationsService = donationsService;
    this.bonusAppcService = bonusAppcService;
  }

  public Single<BonusAppcModel> getBonusAppc() {
    return bonusAppcService.getBonusAppc();
  }

  public Single<AppCoinsAdvertisingModel> getAdvertising(String packageName, int versionCode) {
    return appCoinsService.getValidCampaign(packageName, versionCode);
  }

  public Single<List<Donation>> getDonationsList(String packageName) {
    return donationsService.getDonations(packageName);
  }
}
