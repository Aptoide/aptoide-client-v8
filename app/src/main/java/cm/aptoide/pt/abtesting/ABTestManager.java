package cm.aptoide.pt.abtesting;

import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 15/06/18.
 */

public class ABTestManager {
  private ABTestCenterRepository abTestCenterRepository;

  public ABTestManager(ABTestCenterRepository abTestCenterRepository) {
    this.abTestCenterRepository = abTestCenterRepository;
  }

  public Single<Experiment> getExperiment(ExperimentType experiment) {
    return abTestCenterRepository.getExperiment(experiment)
        .toSingle();
  }

  public Observable<Boolean> recordImpression(ExperimentType experiment) {
    return abTestCenterRepository.recordImpression(experiment);
  }

  public Observable<Boolean> recordAction(ExperimentType experiment) {
    return abTestCenterRepository.recordAction(experiment);
  }

  public enum ExperimentType {
    SHARE_DIALOG("android_implement");

    private String name;

    ExperimentType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

}
