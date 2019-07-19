package cm.aptoide.pt.app;

import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.app.view.donations.DonationsService;
import java.util.List;
import rx.Single;

public class AppCoinsManager {

  private AppCoinsService appCoinsService;
  private DonationsService donationsService;

  public AppCoinsManager(AppCoinsService appCoinsService, DonationsService donationsService) {
    this.appCoinsService = appCoinsService;
    this.donationsService = donationsService;
  }

  public Single<Boolean> getAdvertising(String packageName, int versionCode) {
    return appCoinsService.getValidCampaign(packageName, versionCode);
  }

  public Single<List<Donation>> getDonationsList(String packageName) {
    return donationsService.getDonations(packageName);
  }
}
