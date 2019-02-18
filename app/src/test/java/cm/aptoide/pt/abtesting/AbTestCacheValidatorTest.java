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
        new Experiment(System.currentTimeMillis() - 86600000, "payload", "", false), false));
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
    Assert.assertFalse(cacheValidator.validateExperiment("experiment1"));
  }

  @Test public void invalidateExpiredExperimentTest() {
    Assert.assertFalse(cacheValidator.validateExperiment("experiment2"));
  }

  @Test public void invalidateErrorModelTest() {
    Assert.assertFalse(cacheValidator.validateCache("experiment3"));
  }

  @Test public void invalidateExperimentOverTest() {
    Assert.assertFalse(cacheValidator.validateExperiment("experiment4"));
  }

  @Test public void validateExperimentTest() {
    Assert.assertTrue(cacheValidator.validateExperiment("experiment5"));
  }

  @Test public void validateCacheUpdate() {
    ExperimentModel experimentModel =
        new ExperimentModel(new Experiment(1000, "payload", "", false), false);
    cacheValidator.updateCache("experiment5", experimentModel);
    Assert.assertFalse(cacheValidator.validateExperiment("experiment5"));
  }
}
