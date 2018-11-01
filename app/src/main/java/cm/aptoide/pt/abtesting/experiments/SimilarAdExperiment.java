package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.ApplicationAdResult;
import java.util.List;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * Created by franciscoaleixo on 08/10/2018.
 */

public class SimilarAdExperiment {
  private final String EXPERIMENT_ID = "ASV-1068-AppNext";

  private ABTestManager abTestManager;
  private Scheduler scheduler;
  private AdsManager adsManager;

  public SimilarAdExperiment(ABTestManager abTestManager, Scheduler scheduler,
      AdsManager adsManager) {
    this.abTestManager = abTestManager;
    this.scheduler = scheduler;
    this.adsManager = adsManager;
  }

  public Single<ApplicationAdResult> getSimilarAd(String packageName, List<String> keywords) {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .observeOn(scheduler)
        .flatMapSingle(experiment -> {
          String experimentAssigment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssigment = experiment.getAssignment();
          }
          switch (experimentAssigment) {
            case "appnext_ad":
              return adsManager.loadAppnextAd(keywords);
            case "default":
            case "default_ad":
            default:
              return adsManager.loadAd(packageName, keywords);
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
