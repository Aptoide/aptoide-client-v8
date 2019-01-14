package cm.aptoide.pt.repository.request;

import cm.aptoide.pt.ads.CampaignsService;
import cm.aptoide.pt.ads.CampaignsServiceResponse;
import cm.aptoide.pt.home.RewardApp;
import cm.aptoide.pt.install.InstallManager;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class RewardAppCoinsAppsRepository {

  private InstallManager installManager;
  private CampaignsService campaignsService;

  public RewardAppCoinsAppsRepository(InstallManager installManager,
      CampaignsService campaignsService) {
    this.installManager = installManager;
    this.campaignsService = campaignsService;
  }

  public Observable<List<RewardApp>> getAppCoinsRewardAppsFromHomeMore() {
    return campaignsService.getCampaigns()
        .flatMap(response -> map(response.getCampaigns()));
  }

  private Observable<List<RewardApp>> map(List<CampaignsServiceResponse.Campaign> list) {
    List<RewardApp> rewardAppsList = new ArrayList<>();
    for (CampaignsServiceResponse.Campaign campaign : list) {
      if (!installManager.wasAppEverInstalled(campaign.getPackageName())) {
        rewardAppsList.add(
            new RewardApp(campaign.getLabel(), campaign.getIcon(), campaign.getAverageRating(),
                campaign.getDownloads(), campaign.getPackageName(), campaign.getUid(), "", true,
                true, campaign.getClickUrl(), campaign.getDownloadUrl()));
      }
    }
    return Observable.just(rewardAppsList);
  }
}
