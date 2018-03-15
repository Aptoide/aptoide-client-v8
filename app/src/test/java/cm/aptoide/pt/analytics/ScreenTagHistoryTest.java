package cm.aptoide.pt.analytics;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScreenTagHistoryTest {

  @Test public void checkIfNullFragmentReturnEmptyStringFragment() {
    //Given a ScreenTagHistory with null fragment
    ScreenTagHistory screenTagHistory =
        ScreenTagHistory.Builder.build(null, "tag", StoreContext.home);

    //When fragment getter is requested
    //Then it should return an empty string
    assertFalse(screenTagHistory.getFragment() == null);
    assertTrue(screenTagHistory.getFragment()
        .equals(""));
  }

  @Test public void checkIfNullTagReturnEmptyStringTag() {
    //Given a ScreenTagHistory with null tag
    ScreenTagHistory screenTagHistory =
        ScreenTagHistory.Builder.build("fragment", null, StoreContext.home);

    //When tag getter is requested
    //Then it should return an empty string
    assertFalse(screenTagHistory.getTag() == null);
    assertTrue(screenTagHistory.getTag()
        .equals(""));
  }

  @Test public void checkIfNullStoreReturnEmptyStringStore() {
    //Given a ScreenTagHistory with null storeContext
    ScreenTagHistory screenTagHistory = ScreenTagHistory.Builder.build("fragment", "tag", null);

    //When store getter is requested
    //Then it should return an empty string
    assertFalse(screenTagHistory.getStore() == null);
    assertTrue(screenTagHistory.getStore()
        .equals(""));
  }
}
