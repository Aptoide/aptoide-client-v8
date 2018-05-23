package cm.aptoide.pt.app;

import android.support.annotation.NonNull;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.view.app.AppsList;
import java.util.List;
import rx.Observable;
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

  public Single<MinimalAdRequestResult> loadAd(String packageName, List<String> keyWords) {
    return adsRepository.loadAdsFromAppviewSuggested(packageName, keyWords)
        .flatMap(minimalAds -> Observable.just(new MinimalAdRequestResult(minimalAds.get(0))))
        .toSingle()
        .onErrorReturn(throwable -> createMinimalAdRequestResultError(throwable));
  }

  @NonNull private MinimalAdRequestResult createMinimalAdRequestResultError(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new MinimalAdRequestResult(AppsList.Error.NETWORK);
    } else {
      return new MinimalAdRequestResult(AppsList.Error.GENERIC);
    }
  }
}
