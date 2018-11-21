package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.ads.IronSourceAdRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class IronSourceInterstitialAdExperiment {

  private final String EXPERIMENT_ID = "ASV-1187-IronSource-Video-Interstitial";

  private ABTestManager abTestManager;
  private Scheduler scheduler;
  private IronSourceAdRepository ironSourceAdRepository;

  public IronSourceInterstitialAdExperiment(ABTestManager abTestManager, Scheduler scheduler,
      IronSourceAdRepository ironSourceAdRepository) {
    this.abTestManager = abTestManager;
    this.scheduler = scheduler;
    this.ironSourceAdRepository = ironSourceAdRepository;
  }

  public Observable<Experiment> loadInterstitial() {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .observeOn(scheduler)
        .doOnNext(experiment -> {
          String experimentAssignment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssignment = experiment.getAssignment();
          }
          if (experimentAssignment == null) experimentAssignment = "default";
          switch (experimentAssignment) {
            case "default":
            case "ironsource":
              ironSourceAdRepository.loadInterstitialAd();
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
          if (experimentAssignment == null) experimentAssignment = "default";
          switch (experimentAssignment) {
            case "default":
            case "ironsource":
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
