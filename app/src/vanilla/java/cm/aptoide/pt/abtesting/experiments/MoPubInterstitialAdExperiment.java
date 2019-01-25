package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.ads.MoPubAnalytics;
import rx.Observable;

public class MoPubInterstitialAdExperiment {

  private final String EXPERIMENT_ID = "ASV-1377-MoPub-Ads";
  private final ABTestManager abTestManager;
  private final MoPubAnalytics moPubAnalytics;

  public MoPubInterstitialAdExperiment(ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    this.abTestManager = abTestManager;
    this.moPubAnalytics = moPubAnalytics;
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
              moPubAnalytics.setMoPubAbTestGroup(true);
              return Observable.just(false);
            case "mopub":
              moPubAnalytics.setMoPubAbTestGroup(false);
              return Observable.just(true);
            default:
              return Observable.just(false);
          }
        });
  }

  public Observable<Boolean> recordAdImpression() {
    return abTestManager.recordImpression(EXPERIMENT_ID);
  }

  public Observable<Boolean> recordAdClick() {
    return abTestManager.recordAction(EXPERIMENT_ID);
  }
}
