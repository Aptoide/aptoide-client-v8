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

  public Single<MinimalAd> loadAds(String packageName, String storeName) {
    return adsRepository.loadAdsFromAppView(packageName, storeName)
        .toSingle();
  }

  public Single<MinimalAd> loadAd(String packageName, List<String> keyWords) {
    return adsRepository.loadAdsFromAppviewSuggested(packageName, keyWords)
        .map(minimalAds -> minimalAds.get(0))
        .toSingle();
  }
}
