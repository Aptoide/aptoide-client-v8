package cm.aptoide.pt.abtesting.experiments;

import rx.Single;

public class MoPubInterstitialAdExperiment {

  public MoPubInterstitialAdExperiment() {
  }

  public Single<Boolean> loadInterstitial() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdImpression() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdClick() {
    return Single.just(false);
  }
}
