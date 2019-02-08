package cm.aptoide.pt.abtesting;

import java.util.HashMap;
import rx.Observable;

public class AbTestHelper {

  public AbTestHelper() {
  }

  public Observable<Boolean> recordImpression(HashMap<String, ExperimentModel> localCache,
      String identifier, ABTestService service) {
    if (localCache.containsKey(identifier) && !localCache.get(identifier)
        .hasError() && !localCache.get(identifier)
        .getExperiment()
        .isExperimentOver()) {
      return service.recordImpression(identifier);
    }
    return Observable.just(false);
  }

  public Observable<Void> cacheExperiment(HashMap<String, ExperimentModel> localCache,
      RealmExperimentPersistence persistence, ExperimentModel experiment, String experimentName) {
    if (localCache.containsKey(experimentName)) localCache.remove(experimentName);

    localCache.put(experimentName, experiment);
    persistence.save(experimentName, experiment.getExperiment());
    return Observable.just(null);
  }
}
