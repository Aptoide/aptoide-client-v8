package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.database.RoomExperimentPersistence;
import java.util.HashMap;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by franciscocalado on 18/06/18.
 */

public class ABTestCenterRepository implements AbTestRepository {

  private final ABTestService service;
  private final RoomExperimentPersistence persistence;
  private final HashMap<String, ExperimentModel> localCache;
  private final AbTestCacheValidator cacheValidator;

  public ABTestCenterRepository(ABTestService service, HashMap<String, ExperimentModel> localCache,
      RoomExperimentPersistence persistence, AbTestCacheValidator cacheValidator) {
    this.service = service;
    this.localCache = localCache;
    this.persistence = persistence;
    this.cacheValidator = cacheValidator;
  }

  public Observable<Experiment> getExperiment(String identifier,
      BaseExperiment.ExperimentType type) {
    if (localCache.containsKey(identifier)) {
      if (cacheValidator.isExperimentValid(identifier)) {
        return Observable.just(localCache.get(identifier)
            .getExperiment());
      } else {
        return service.getExperiment(identifier, type)
            .flatMap(experimentToCache -> cacheExperiment(experimentToCache, identifier).andThen(
                Observable.just(experimentToCache.getExperiment())));
      }
    }
    return persistence.get(identifier)
        .toObservable()
        .observeOn(Schedulers.io())
        .flatMap(model -> {
          if (!model.hasError() && !model.getExperiment()
              .isExpired()) {
            if (!localCache.containsKey(identifier)) {
              localCache.put(identifier, model);
            }
            return Observable.just(model.getExperiment());
          } else {
            return service.getExperiment(identifier, type)
                .flatMap(
                    experimentToCache -> cacheExperiment(experimentToCache, identifier).andThen(
                        Observable.just(experimentToCache.getExperiment())));
          }
        });
  }

  public Observable<Boolean> recordImpression(String identifier,
      BaseExperiment.ExperimentType type) {
    if (cacheValidator.isCacheValid(identifier)) {
      return service.recordImpression(identifier, type);
    }
    return Observable.just(false);
  }

  public Observable<Boolean> recordAction(String identifier, BaseExperiment.ExperimentType type) {
    if (cacheValidator.isCacheValid(identifier)) {
      return getExperiment(identifier, null).flatMap(
          experiment -> service.recordAction(identifier, experiment.getAssignment(), type));
    }
    return Observable.just(false);
  }

  @Override public Observable<Boolean> recordAction(String identifier, int position,
      BaseExperiment.ExperimentType type) {
    return recordAction(identifier, type);
  }

  public Completable cacheExperiment(ExperimentModel experiment, String experimentName) {
    return Completable.fromAction(() -> localCache.put(experimentName, experiment))
        .andThen(persistence.save(experimentName, experiment.getExperiment()));
  }

  @Override public Observable<String> getExperimentId(String id) {
    return Observable.just(id);
  }
}

