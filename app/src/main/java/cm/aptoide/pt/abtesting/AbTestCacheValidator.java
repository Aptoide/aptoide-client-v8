package cm.aptoide.pt.abtesting;

import java.util.HashMap;

public class AbTestCacheValidator {

  private HashMap<String, ExperimentModel> localCache;

  public AbTestCacheValidator(HashMap<String, ExperimentModel> localCache) {
    this.localCache = localCache;
  }

  public boolean validateCache(String id) {
    return localCache.containsKey(id) && !localCache.get(id)
        .hasError() && !localCache.get(id)
        .getExperiment()
        .isExperimentOver();
  }

  public boolean validateExperiment(String id) {
    return !localCache.get(id)
        .getExperiment()
        .isExpired() && !localCache.get(id)
        .hasError() && !localCache.get(id)
        .getExperiment()
        .isExperimentOver() && localCache.get(id)
        .getExperiment()
        .isPartOfExperiment();
  }

  public void updateCache(String id, ExperimentModel experiment) {
    if (localCache.containsKey(id)) localCache.remove(id);
    localCache.put(id, experiment);
  }
}
