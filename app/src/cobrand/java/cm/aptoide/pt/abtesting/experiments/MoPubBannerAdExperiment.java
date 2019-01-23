package cm.aptoide.pt.abtesting.experiments;

import rx.Observable;

public class MoPubBannerAdExperiment {

  public MoPubBannerAdExperiment() {
  }

  public Observable<Boolean> shouldLoadBanner() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdImpression() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdClick() {
    return Observable.just(false);
  }
}
