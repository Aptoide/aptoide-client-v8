package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import rx.Observable;

/**
 * Created by franciscocalado on 08/10/2018.
 */

public class NotificationsExperiment {
  private static final String EXPERIMENT_ID = "ASV-1037_notifications";

  private ABTestManager abTestManager;

  public NotificationsExperiment(ABTestManager abTestManager) {
    this.abTestManager = abTestManager;
  }

  public Observable<Boolean> performAbTest() {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .flatMap(experiment -> {
          String experimentAssigment = "default";
          if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
            experimentAssigment = experiment.getAssignment();
          }
          switch (experimentAssigment) {
            case "default":
            case "show":
              return Observable.just(true);
            case "hide":
              return Observable.just(false);
          }
          return Observable.error(new Throwable());
        });
  }
}
