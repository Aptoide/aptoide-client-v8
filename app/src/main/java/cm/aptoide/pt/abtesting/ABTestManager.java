package cm.aptoide.pt.abtesting;

import rx.Observable;

/**
 * Created by franciscocalado on 15/06/18.
 */

public class ABTestManager {
  private AbTestRepository abTestRepository;

  public ABTestManager(AbTestRepository abTestRepository) {
    this.abTestRepository = abTestRepository;
  }

  public Observable<Experiment> getExperiment(String identifier,
      BaseExperiment.ExperimentType type) {
    return abTestRepository.getExperiment(identifier, type)
        .first();
  }

  public Observable<Boolean> recordImpression(String identifier,
      BaseExperiment.ExperimentType type) {
    return abTestRepository.recordImpression(identifier, type);
  }

  public Observable<Boolean> recordAction(String identifier, BaseExperiment.ExperimentType type) {
    return abTestRepository.recordAction(identifier, type);
  }

  public Observable<Boolean> recordAction(String identifier, int position,
      BaseExperiment.ExperimentType type) {
    return abTestRepository.recordAction(identifier, position, type);
  }

  public Observable<String> getExperimentId(String id) {
    return abTestRepository.getExperimentId(id);
  }
}
