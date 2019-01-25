package cm.aptoide.pt.abtesting.experiments;

import rx.Single;

public class MoPubNativeAdExperiment {

  private final String EXPERIMENT_ID = "ASV-1377-MoPub-Ads";

  public MoPubNativeAdExperiment() {
  }

  public Single<Boolean> shouldLoadNative() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdImpression() {
    return Single.just(false);
  }

  public Single<Boolean> recordAdClick() {
    return Single.just(false);
  }
}
