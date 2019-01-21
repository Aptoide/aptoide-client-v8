package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import rx.Observable;

public class MoPubInterstitialAdExperiment {

  private final String EXPERIMENT_ID = "ASV-1377-MoPub-Video-Interstitial";
  private final ABTestManager abTestManager;

  public MoPubInterstitialAdExperiment(ABTestManager abTestManager) {
    this.abTestManager = abTestManager;
  }

  public Observable<Boolean> loadInterstitial() {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .flatMap(experiment -> {
          String experimentAssignment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssignment = experiment.getAssignment();
          }
          switch (experimentAssignment) {
            case "default":
            case "control_group":
              // TODO: 1/21/19 analytics controlgroup
              return Observable.just(false);
            case "ironsource":
              return Observable.just(true);
            //ironSourceAdRepository.loadInterstitialAd();
            // TODO: 1/21/19 analytics mopub
            default:
              return Observable.just(false);
          }
        });
  }
}
