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

  public Observable<Experiment> getExperiment(String identifier) {
    return abTestCenterRepository.getExperiment(identifier)
        .first();
  }

  public Observable<Boolean> recordImpression(String identifier) {
    return abTestCenterRepository.recordImpression(identifier);
  }

  public Observable<Boolean> recordAction(String identifier) {
    return abTestCenterRepository.recordAction(identifier);
  }
}
