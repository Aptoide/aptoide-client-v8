package cm.aptoide.pt.abtesting;

import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbTestCacheValidatorTest {

  private AbTestCacheValidator cacheValidator;

  @Before public void setUpAbTestCacheValidatorTest() {
    HashMap<String, ExperimentModel> localCache = new HashMap<>();
    localCache.put("experiment1", new ExperimentModel(new Experiment(), false));
    localCache.put("experiment2", new ExperimentModel(
        new Experiment(System.currentTimeMillis() - Experiment.MAX_CACHE_TIME_IN_MILLIS - 1000,
            "payload", "", false), false));
    localCache.put("experiment3", new ExperimentModel(new Experiment(), true));
    localCache.put("experiment4",
        new ExperimentModel(new Experiment(System.currentTimeMillis(), "payload", "", true),
            false));
    localCache.put("experiment5",
        new ExperimentModel(new Experiment(System.currentTimeMillis(), "payload", "", false),
            false));
    cacheValidator = new AbTestCacheValidator(localCache);
  }

  @Test public void invalidateEmptyExperimentNoErrorTest() {
    Assert.assertFalse(cacheValidator.isExperimentValid("experiment1"));
  }

  @Test public void invalidateExpiredExperimentTest() {
    System.out.println("AbTestCacheValidatorTest.invalidateExpiredExperimentTest "
        + cacheValidator.isExperimentValid("experiment2"));
    Assert.assertFalse(cacheValidator.isExperimentValid("experiment2"));
  }

  @Test public void invalidateErrorModelTest() {
    Assert.assertFalse(cacheValidator.isCacheValid("experiment3"));
  }

  @Test public void invalidateExperimentOverTest() {
    Assert.assertFalse(cacheValidator.isExperimentValid("experiment4"));
  }

  @Test public void validateExperimentTest() {
    Assert.assertTrue(cacheValidator.isExperimentValid("experiment5"));
  }
}
