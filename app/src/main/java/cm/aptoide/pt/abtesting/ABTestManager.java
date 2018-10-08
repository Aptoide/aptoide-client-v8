package cm.aptoide.pt.abtesting;

import rx.Observable;

/**
 * Created by franciscocalado on 15/06/18.
 */

public class ABTestManager {
  private ABTestCenterRepository abTestCenterRepository;

  public ABTestManager(ABTestCenterRepository abTestCenterRepository) {
    this.abTestCenterRepository = abTestCenterRepository;
  }

  public Observable<Experiment> getExperiment(ExperimentType experiment) {
    return abTestCenterRepository.getExperiment(experiment)
        .first();
  }

  public Observable<Boolean> recordImpression(ExperimentType experiment) {
    return abTestCenterRepository.recordImpression(experiment);
  }

  public Observable<Boolean> recordAction(ExperimentType experiment) {
    return abTestCenterRepository.recordAction(experiment);
  }

  public enum ExperimentType {
    SHARE_DIALOG("ASV-recommend_continue_test"),
    SIMILAR_AD("ASV-1068-AppNext_Test");

    private String name;

    ExperimentType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
