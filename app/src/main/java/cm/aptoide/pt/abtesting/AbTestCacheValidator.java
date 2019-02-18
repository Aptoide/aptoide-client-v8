package cm.aptoide.pt.abtesting;

import java.util.HashMap;

public class AbTestCacheValidator {

  private HashMap<String, ExperimentModel> localCache;

  public AbTestCacheValidator(HashMap<String, ExperimentModel> localCache) {
    this.localCache = localCache;
  }

  public boolean isCacheValid(String experimentId) {
    return localCache.containsKey(experimentId) && !localCache.get(experimentId)
        .hasError() && !localCache.get(experimentId)
        .getExperiment()
        .isExperimentOver();
  }

  public boolean isExperimentValid(String experimentId) {
    ExperimentModel model = localCache.get(experimentId);
    return !model.getExperiment()
        .isExpired() && !model.hasError() && !model.getExperiment()
        .isExperimentOver() && model.getExperiment()
        .isPartOfExperiment();
  }
}
