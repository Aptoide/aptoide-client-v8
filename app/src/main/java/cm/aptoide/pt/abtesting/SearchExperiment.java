package cm.aptoide.pt.abtesting;

import rx.Observable;

public class SearchExperiment {

  private final String EXPERIMENT_ID = "search";
  private final ABTestManager abTestManager;

  public SearchExperiment(ABTestManager abTestManager) {
    this.abTestManager = abTestManager;
  }

  public Observable<SearchExperimentResult> loadExperiment() {
    return abTestManager.getExperiment(EXPERIMENT_ID)
        .flatMap(experiment -> abTestManager.getExperimentId(EXPERIMENT_ID)
            .map(experimentId -> {
              if (!experiment.isExperimentOver() && experiment.isPartOfExperiment()) {
                return new SearchExperimentResult(experimentId, experiment.getAssignment());
              } else {
                return new SearchExperimentResult("no_experiment", "control");
              }
            }));
  }

  public Observable<Boolean> recordImpression() {
    return abTestManager.recordImpression(EXPERIMENT_ID);
  }

  public Observable<Boolean> recordAction(int position) {
    return abTestManager.recordAction(EXPERIMENT_ID, position);
  }

  public static class SearchExperimentResult {
    private String experimentId;
    private String experimentGroup;

    public SearchExperimentResult(String experimentId, String experimentGroup) {
      this.experimentId = experimentId;
      this.experimentGroup = experimentGroup;
    }

    public String getExperimentId() {
      return experimentId;
    }

    public String getExperimentGroup() {
      return experimentGroup;
    }
  }
}
