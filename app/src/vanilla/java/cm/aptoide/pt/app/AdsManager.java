package cm.aptoide.pt.app;

import android.support.annotation.NonNull;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.view.app.AppsList;
import java.util.List;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

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

  public Single<MinimalAdRequestResult> loadAd(String packageName, List<String> keyWords) {
    return adsRepository.loadAdsFromAppviewSuggested(packageName, keyWords)
        .flatMap(minimalAds -> Observable.just(new MinimalAdRequestResult(minimalAds.get(0))))
        .toSingle()
        .onErrorReturn(throwable -> createMinimalAdRequestResultError(throwable));
  }

  public Single<AppNextAdResult> loadAppnextAd(List<String> keywords, String placementId) {
    return adsRepository.loadAppNextAd(keywords, placementId)
        .toSingle();
  }

  public PublishSubject<AppNextAdResult> appNextAdClick() {
    return adsRepository.appNextAdClick();
  }

  @NonNull private MinimalAdRequestResult createMinimalAdRequestResultError(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new MinimalAdRequestResult(AppsList.Error.NETWORK);
    } else {
      return new MinimalAdRequestResult(AppsList.Error.GENERIC);
    }
  }

  public void handleAdsLogic(SearchAdResult searchAdResult) {
    storedMinimalAdAccessor.insert(adMapper.map(searchAdResult, null));
    AdNetworkUtils.knockCpc(adMapper.map(searchAdResult));
  }
}
