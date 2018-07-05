package cm.aptoide.pt.abtesting;

import java.util.HashMap;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by franciscocalado on 18/06/18.
 */

public class ABTestCenterRepository {

  private ABTestService service;
  private RealmExperimentPersistence persistence;
  private HashMap<String, ExperimentModel> localCache;

  public ABTestCenterRepository(ABTestService service, HashMap<String, ExperimentModel> localCache,
      RealmExperimentPersistence persistence) {
    this.service = service;
    this.localCache = localCache;
    this.persistence = persistence;
  }

  public Observable<Experiment> getExperiment(ABTestManager.ExperimentType experiment) {
    if (localCache.containsKey(experiment.getName())) {
      if (!localCache.get(experiment.getName())
          .getExperiment()
          .isExpired() && !localCache.get(experiment.getName())
          .hasError() && !localCache.get(experiment.getName())
          .getExperiment()
          .isExperimentOver()) {
        return Observable.just(localCache.get(experiment.getName())
            .getExperiment());
      } else {
        return service.getExperiment(experiment)
            .flatMap(experimentToCache -> cacheExperiment(experimentToCache,
                experiment.getName()).flatMap(
                __ -> Observable.just(experimentToCache.getExperiment())));
      }
    }
    return persistence.get(ABTestManager.ExperimentType.SHARE_DIALOG)
        .observeOn(Schedulers.io())
        .flatMap(model -> {
          if (!model.hasError() && !model.getExperiment()
              .isExpired()) {
            if (!localCache.containsKey(experiment.getName())) {
              localCache.put(experiment.getName(), model);
            }
            return Observable.just(model.getExperiment());
          } else {
            return service.getExperiment(experiment)
                .flatMap(experimentToCache -> cacheExperiment(experimentToCache,
                    experiment.getName()).flatMap(
                    __ -> Observable.just(experimentToCache.getExperiment())));
          }
        });
  }

  public Observable<Boolean> recordImpression(ABTestManager.ExperimentType experimentType) {
    if (localCache.containsKey(experimentType.getName()) && !localCache.get(
        experimentType.getName())
        .hasError() && !localCache.get(experimentType.getName())
        .getExperiment()
        .isExperimentOver()) {
      return service.recordImpression(experimentType);
    }
    return Observable.just(false);
  }

  public Observable<Boolean> recordAction(ABTestManager.ExperimentType experimentType) {
    if (localCache.containsKey(experimentType.getName()) && !localCache.get(
        experimentType.getName())
        .hasError() && !localCache.get(experimentType.getName())
        .getExperiment()
        .isExperimentOver()) {
      return getExperiment(experimentType).flatMap(
          experiment -> service.recordAction(experimentType, experiment.getAssignment()));
    }
    return Observable.just(false);
  }

  private Observable<Void> cacheExperiment(ExperimentModel experiment, String experimentName) {

    if (localCache.containsKey(experimentName)) localCache.remove(experimentName);

    localCache.put(experimentName, experiment);
    persistence.save(experimentName, experiment.getExperiment());
    return Observable.just(null);
  }
}

