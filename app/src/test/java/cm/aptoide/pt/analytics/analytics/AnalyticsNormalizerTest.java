package cm.aptoide.pt.analytics.analytics;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jdandrade on 27/02/2018.
 */

public class AnalyticsNormalizerTest {

  private AnalyticsNormalizer analyticsNormalizer;

  @Before public void setupAnalyticsNormalizer() {
    analyticsNormalizer = new AnalyticsNormalizer();
  }

  @Test public void normalizeNullEventAttributeValuesToEmptyString() {
    String key1 = "key1";
    String key3 = "key3";
    String key2 = "key2";

    //Given a event data Map with non null key attribute
    Map<String, Object> data = new HashMap<>();
    data.put(key1, "value1");
    data.put(key2, 2);

    //And null value attribute
    data.put(key3, null);

    //When the attributes get normalized by the AnalyticsNormalized
    data = analyticsNormalizer.normalize(data);

    //Then data with all key-values is returned
    assertTrue(data.containsKey(key1));
    assertTrue(data.containsKey(key2));
    assertTrue(data.containsKey(key3));

    //And null values are set to empty strings
    assertFalse(data.containsValue(null));
    assertTrue(data.get(key3)
        .equals(""));
  }
}
