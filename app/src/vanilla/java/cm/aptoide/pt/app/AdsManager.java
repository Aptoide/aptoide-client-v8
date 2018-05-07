package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.database.realm.MinimalAd;
import java.util.List;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AdsManager {

  private final AdsRepository adsRepository;

  public AdsManager(AdsRepository adsRepository) {

    this.adsRepository = adsRepository;
  }

  public Single<MinimalAd> loadAd(String packageName, String storeName) {
    return adsRepository.getAdsFromAppView(packageName, storeName)
        .toSingle();
  }

  public Single<List<MinimalAd>> loadSuggestedApps(String packageName, List<String> keyWords) {
    return adsRepository.getAdsFromAppviewSuggested(packageName, keyWords)
        .toSingle();
  }
}
