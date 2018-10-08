package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.ApplicationAdResult;
import java.util.List;
import rx.Scheduler;
import rx.Single;

/**
 * Created by franciscoaleixo on 08/10/2018.
 */

public class SimilarAdExperiment {
  private final String IDENTIFIER = "SimilarAdExperiment";

  private ABTestManager abTestManager;
  private Scheduler scheduler;
  private AdsManager adsManager;

  public SimilarAdExperiment(ABTestManager abTestManager, Scheduler scheduler, AdsManager adsManager) {
    this.abTestManager = abTestManager;
    this.scheduler = scheduler;
    this.adsManager = adsManager;
  }

  public Single<ApplicationAdResult> getSimilarAd(String packageName, List<String> keywords){
    return abTestManager.getExperiment(ABTestManager.ExperimentType.SIMILAR_AD)
        .flatMapSingle(experiment -> {
          String experimentAssigment = "default";
          if(!experiment.isExperimentOver() && experiment.isPartOfExperiment()){
            experimentAssigment = experiment.getAssignment();
          }
          switch (experimentAssigment){
            case "appnext_ad":
              return adsManager.loadAppnextAd(keywords);
            case "default":
            case "default_ad":
            default:
              return adsManager.loadAd(packageName, keywords);
          }
        })
        .toSingle()
        .observeOn(scheduler);
  }

}
