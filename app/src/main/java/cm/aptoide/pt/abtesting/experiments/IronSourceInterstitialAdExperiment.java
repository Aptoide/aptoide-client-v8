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
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .doOnNext(experiment -> {
          String experimentAssignment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssignment = experiment.getAssignment();
          }
          switch (experimentAssignment) {
            case "default":
            case "control_group":
              ironSourceAnalytics.setIronSourceAbTestGroup(true);
              break;
            case "ironsource":
              ironSourceAdRepository.loadInterstitialAd();
              ironSourceAnalytics.setIronSourceAbTestGroup(false);
          }
        });
  }

  public Single<Experiment> showInterstitial() {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .observeOn(scheduler)
        .doOnNext(experiment -> {
          String experimentAssignment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssignment = experiment.getAssignment();
          }
          switch (experimentAssignment) {
            case "default":
            case "control_group":
              ironSourceAnalytics.setIronSourceAbTestGroup(true);
              break;
            case "ironsource":
              ironSourceAnalytics.setIronSourceAbTestGroup(false);
              ironSourceAdRepository.showInterstitialAd();
          }
        })
        .toSingle();
  }

  public Observable<Boolean> recordAdImpression() {
    return abTestManager.recordImpression(EXPERIMENT_ID);
  }

  public Observable<Boolean> recordAdClick() {
    return abTestManager.recordAction(EXPERIMENT_ID);
  }
}
