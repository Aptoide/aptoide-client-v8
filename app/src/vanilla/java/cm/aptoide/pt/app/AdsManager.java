package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.database.realm.MinimalAd;
import rx.Observable;

/**
 * Created by D01 on 04/05/18.
 */

public class AdsManager {

  private final AdsRepository adsRepository;

  public AdsManager(AdsRepository adsRepository) {

    this.adsRepository = adsRepository;
  }

  public Observable<MinimalAd> getAdsFromAppView(String packageName, String storeName) {
    return adsRepository.getAdsFromAppView(packageName, storeName);
  }
}
