package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.ads.IronSourceAdRepository;
import cm.aptoide.pt.ads.IronSourceAnalytics;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class IronSourceInterstitialAdExperiment {

  private final String EXPERIMENT_ID = "ASV-1187-IronSource-Video-Interstitial2";

  private ABTestManager abTestManager;
  private Scheduler scheduler;
  private IronSourceAdRepository ironSourceAdRepository;
  private IronSourceAnalytics ironSourceAnalytics;

  public IronSourceInterstitialAdExperiment(ABTestManager abTestManager, Scheduler scheduler,
      IronSourceAdRepository ironSourceAdRepository, IronSourceAnalytics ironSourceAnalytics) {
    this.abTestManager = abTestManager;
    this.scheduler = scheduler;
    this.ironSourceAdRepository = ironSourceAdRepository;
    this.ironSourceAnalytics = ironSourceAnalytics;
  }

  public Observable<Experiment> loadInterstitial() {
    return Observable.just(new Experiment());
  }

  public Single<Experiment> showInterstitial() {
    return Single.just(new Experiment());
  }

  public Observable<Boolean> recordAdImpression() {
    return abTestManager.recordImpression(EXPERIMENT_ID);
  }

  public Observable<Boolean> recordAdClick() {
    return abTestManager.recordAction(EXPERIMENT_ID);
  }
}
