package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.ads.IronSourceAnalytics;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * Dummy class
 */
public class IronSourceInterstitialAdExperiment {

  public IronSourceInterstitialAdExperiment(ABTestManager abTestManager, Scheduler scheduler,
      IronSourceAdRepository ironSourceAdRepository, IronSourceAnalytics ironSourceAnalytics) {
  }

  public Observable<Experiment> loadInterstitial() {
    return Observable.just(new Experiment());
  }

  public Single<Experiment> showInterstitial() {
    return Single.just(new Experiment());
  }

  public Observable<Boolean> recordAdImpression() {
    return Observable.just(false);
  }

  public Observable<Boolean> recordAdClick() {
    return Observable.just(false);
  }
}
