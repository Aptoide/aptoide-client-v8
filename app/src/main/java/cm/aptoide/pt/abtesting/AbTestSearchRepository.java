package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.autoupdate.AbSearchGroupResponse;
import java.util.HashMap;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AbTestSearchRepository implements AbTestRepository {
  private SearchAbTestService searchAbTestService;
  private ABTestService service;
  private RealmExperimentPersistence persistence;
  private HashMap<String, ExperimentModel> localCache;
  private AbSearchGroupResponse abSearchGroupResponse;
  private AbTestCacheValidator cacheValidator;

  public AbTestSearchRepository(ABTestService service, HashMap<String, ExperimentModel> localCache,
      RealmExperimentPersistence persistence, SearchAbTestService searchAbTestService,
      AbTestCacheValidator cacheValidator) {
    this.searchAbTestService = searchAbTestService;
    this.service = service;
    this.persistence = persistence;
    this.localCache = localCache;
    this.cacheValidator = cacheValidator;
  }

  public Observable<Experiment> getExperiment(String identifier) {
    return getExperimentId(identifier).flatMap(this::resolveExperiment);
  }

  @Override public Observable<Boolean> recordImpression(String identifier) {
    return getExperimentId(identifier).flatMap(id -> {
      if (cacheValidator.isCacheValid(id)) {
        return service.recordImpression(id);
      }
      return Observable.just(false);
    });
  }

  @Override public Observable<Boolean> recordAction(String identifier) {
    return getExperimentId(identifier).flatMap(id -> {
      if (cacheValidator.isCacheValid(id)) {
        return getExperiment(identifier).flatMap(
            experiment -> service.recordAction(id, experiment.getAssignment()));
      }
      return Observable.just(false);
    });
  }

  @Override public Observable<Boolean> recordAction(String identifier, int position) {
    return getExperimentResponse(identifier).flatMap(response -> {
      if (cacheValidator.isCacheValid(response.getAbSearchId())) {
        return getExperiment(identifier).flatMap(experiment -> {
          if (position < response.getItems()) {
            return service.recordAction(response.getAbSearchId(), experiment.getAssignment());
          }
          return Observable.just(false);
        });
      }
      return Observable.just(false);
    });
  }

  @Override
  public Observable<Void> cacheExperiment(ExperimentModel experiment, String experimentName) {
    localCache.put(experimentName, experiment);
    persistence.save(experimentName, experiment.getExperiment());
    return Observable.just(null);
  }

  @Override public Observable<String> getExperimentId(String id) {
    if (id.equals("search")) {
      if (abSearchGroupResponse != null && abSearchGroupResponse.getAbSearchId() != null) {
        return Observable.just(abSearchGroupResponse.getAbSearchId());
      } else {
        return searchAbTestService.getExperimentForSearchAbTest()
            .map(result -> {
              this.abSearchGroupResponse = result;
              return abSearchGroupResponse.getAbSearchId();
            });
      }
    }
    return Observable.just(id);
  }

  private Observable<AbSearchGroupResponse> getExperimentResponse(String id) {
    if (id.equals("search")) {
      if (abSearchGroupResponse.getAbSearchId() != null) {
        return Observable.just(abSearchGroupResponse);
      } else {
        return searchAbTestService.getExperimentForSearchAbTest()
            .map(result -> {
              this.abSearchGroupResponse = result;
              return abSearchGroupResponse;
            });
      }
    }
    return Observable.just(null);
  }

  private Observable<Experiment> resolveExperiment(String experimentId) {
    if (localCache.containsKey(experimentId)) {
      if (cacheValidator.isExperimentValid(experimentId)) {
        return Observable.just(localCache.get(experimentId)
            .getExperiment());
      } else {
        return searchAbTestService.getExperimentForSearchAbTest()
            .flatMap(response -> {
              this.abSearchGroupResponse = response;
              return service.getExperiment(experimentId)
                  .flatMap(
                      experimentToCache -> cacheExperiment(experimentToCache, experimentId).flatMap(
                          __ -> Observable.just(experimentToCache.getExperiment())));
            });
      }
    }
    return persistence.get(experimentId)
        .observeOn(Schedulers.io())
        .flatMap(model -> {
          if (!model.hasError() && !model.getExperiment()
              .isExpired()) {
            if (!localCache.containsKey(experimentId)) {
              localCache.put(experimentId, model);
            }
            return Observable.just(model.getExperiment());
          } else {
            return searchAbTestService.getExperimentForSearchAbTest()
                .flatMap(result -> {
                  this.abSearchGroupResponse = result;
                  return service.getExperiment(experimentId)
                      .flatMap(experimentToCache -> cacheExperiment(experimentToCache,
                          experimentId).flatMap(
                          __ -> Observable.just(experimentToCache.getExperiment())));
                });
          }
        });
  }
}
