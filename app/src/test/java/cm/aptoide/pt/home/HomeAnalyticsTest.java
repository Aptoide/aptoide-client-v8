package cm.aptoide.pt.home;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static cm.aptoide.pt.home.HomeAnalytics.TAP_ON_APP;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jdandrade on 16/04/2018.
 */
public class HomeAnalyticsTest {
  @Mock private AnalyticsManager analyticsManager;
  @Mock private NavigationTracker navigationTracker;
  private HomeAnalytics homeAnalytics;

  @Before public void setup() {
    MockitoAnnotations.initMocks(this);
    homeAnalytics = new HomeAnalytics(navigationTracker, analyticsManager);

    when(navigationTracker.getViewName(true)).thenReturn("home");
  }

  @Test public void sendTapOnAppInteractEvent() throws Exception {
    //Given an initialized HomeAnalytics
    //When user clicks on an App
    homeAnalytics.sendTapOnAppInteractEvent(4.7, "cm.aptoide.pt", 2, 5, "Best Apps", 9);
    //Then an Home_Interact event with 'tap on app' action,  app_rating, position, package_name, bundle_name, bundle_position and bundle_total_items is logged to the analytics manager
    HashMap<String, Object> data = new HashMap<>();
    data.put("action", TAP_ON_APP);
    data.put("app_rating", 4.7);
    data.put("package_name", "cm.aptoide.pt");
    data.put("app_position", 2);
    data.put("bundle_name", "Best Apps");
    data.put("bundle_position", 5);
    data.put("bundle_total_items", 9);
    verify(analyticsManager).logEvent(eq(data), eq("Home_Interact"),
        eq(AnalyticsManager.Action.CLICK), eq("home"));
  }
}