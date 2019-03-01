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

  public Observable<Experiment> getExperiment(String identifier) {
    return abTestRepository.getExperiment(identifier)
        .first();
  }

  public Observable<Boolean> recordImpression(String identifier) {
    return abTestRepository.recordImpression(identifier);
  }

  public Observable<Boolean> recordAction(String identifier) {
    return abTestRepository.recordAction(identifier);
  }

  public Observable<Boolean> recordAction(String identifier, int position) {
    return abTestRepository.recordAction(identifier, position);
  }

  public Observable<String> getExperimentId(String id) {
    return abTestRepository.getExperimentId(id);
  }
}
