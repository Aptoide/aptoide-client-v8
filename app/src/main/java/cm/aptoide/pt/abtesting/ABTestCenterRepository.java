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

  public Observable<Experiment> getExperiment(String identifier) {
    if (localCache.containsKey(identifier)) {
      if (!localCache.get(identifier)
          .getExperiment()
          .isExpired() && !localCache.get(identifier)
          .hasError() && !localCache.get(identifier)
          .getExperiment()
          .isExperimentOver()) {
        return Observable.just(localCache.get(identifier)
            .getExperiment());
      } else {
        return service.getExperiment(identifier)
            .flatMap(experimentToCache -> cacheExperiment(experimentToCache, identifier).flatMap(
                __ -> Observable.just(experimentToCache.getExperiment())));
      }
    }
    return persistence.get(identifier)
        .observeOn(Schedulers.io())
        .flatMap(model -> {
          if (!model.hasError() && !model.getExperiment()
              .isExpired()) {
            if (!localCache.containsKey(identifier)) {
              localCache.put(identifier, model);
            }
            return Observable.just(model.getExperiment());
          } else {
            return service.getExperiment(identifier)
                .flatMap(
                    experimentToCache -> cacheExperiment(experimentToCache, identifier).flatMap(
                        __ -> Observable.just(experimentToCache.getExperiment())));
          }
        });
  }

  public Observable<Boolean> recordImpression(String identifier) {
    if (localCache.containsKey(identifier) && !localCache.get(identifier)
        .hasError() && !localCache.get(identifier)
        .getExperiment()
        .isExperimentOver()) {
      return service.recordImpression(identifier);
    }
    return Observable.just(false);
  }

  public Observable<Boolean> recordAction(String identifier) {
    if (localCache.containsKey(identifier) && !localCache.get(identifier)
        .hasError() && !localCache.get(identifier)
        .getExperiment()
        .isExperimentOver()) {
      return getExperiment(identifier).flatMap(
          experiment -> service.recordAction(identifier, experiment.getAssignment()));
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

