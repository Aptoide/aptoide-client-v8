package cm.aptoide.pt.abtesting.experiments;

import rx.Observable;

public class MoPubInterstitialAdExperiment {

  public MoPubInterstitialAdExperiment() {
  }

  public Observable<Boolean> loadInterstitial() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdImpression() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdClick() {
    return Observable.just(false);
  }
}
