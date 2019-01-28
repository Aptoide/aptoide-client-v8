package cm.aptoide.pt.abtesting.experiments;

import rx.Single;

public class MoPubBannerAdExperiment {

  public MoPubBannerAdExperiment() {
  }

  public Single<Boolean> shouldLoadBanner() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdImpression() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdClick() {
    return Single.just(false);
  }
}
