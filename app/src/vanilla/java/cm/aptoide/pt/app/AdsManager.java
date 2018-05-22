package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.search.model.SearchAdResult;
import java.util.List;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AdsManager {

  private final AdsRepository adsRepository;
  private final StoredMinimalAdAccessor storedMinimalAdAccessor;
  private final MinimalAdMapper adMapper;

  public AdsManager(AdsRepository adsRepository, StoredMinimalAdAccessor storedMinimalAdAccessor,
      MinimalAdMapper adMapper) {
    this.adsRepository = adsRepository;
    this.storedMinimalAdAccessor = storedMinimalAdAccessor;
    this.adMapper = adMapper;
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

  public void handleAdsLogic(SearchAdResult searchAdResult) {
    storedMinimalAdAccessor.insert(adMapper.map(searchAdResult, null));
    AdNetworkUtils.knockCpc(adMapper.map(searchAdResult));
  }
}
