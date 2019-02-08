package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.autoupdate.AptoideImgsService;
import java.util.HashMap;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AbTestSearchRepository implements AbTestRepository {
  private AptoideImgsService aptoideImgsService;
  private ABTestService service;
  private RealmExperimentPersistence persistence;
  private HashMap<String, ExperimentModel> localCache;

  public AbTestSearchRepository(ABTestService service, HashMap<String, ExperimentModel> localCache,
      RealmExperimentPersistence persistence, AptoideImgsService aptoideImgsService) {
    this.aptoideImgsService = aptoideImgsService;
    this.service = service;
    this.persistence = persistence;
    this.localCache = localCache;
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
        return aptoideImgsService.getExperimentForSearchAbTest()
            .flatMap(searchId -> service.getExperiment(searchId))
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
            return aptoideImgsService.getExperimentForSearchAbTest()
                .flatMap(searchId -> service.getExperiment(searchId))
                .flatMap(
                    experimentToCache -> cacheExperiment(experimentToCache, identifier).flatMap(
                        __ -> Observable.just(experimentToCache.getExperiment())));
          }
        });
  }

  @Override public Observable<Boolean> recordImpression(String identifier) {
    return null;
  }

  @Override public Observable<Boolean> recordAction(String identifier) {
    return null;
  }

  @Override
  public Observable<Void> cacheExperiment(ExperimentModel experiment, String experimentName) {
    return null;
  }
}
