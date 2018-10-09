package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import rx.Observable;

/**
 * Created by franciscoaleixo on 21/09/2018.
 */

public class ApkFyExperiment {
  private static final String EXPERIMENT_ID = "apkfy";

  private ABTestManager abTestManager;

  public ApkFyExperiment(ABTestManager abTestManager) {
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
            case "old_dialogue":
              return Observable.just(true);
            case "newdialog":
              return Observable.just(false);
          }
          return Observable.error(new Throwable());
        });
  }
}
