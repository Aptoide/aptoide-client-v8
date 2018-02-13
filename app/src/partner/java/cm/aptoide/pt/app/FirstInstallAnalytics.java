package cm.aptoide.pt.app;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by diogoloureiro on 25/10/2017.
 *
 * First install analytics class implementation
 */

public class FirstInstallAnalytics {

  public static final String FIRST_INSTALL_POP_UP = "First_Install_Pop_up";
  public static final String FIRST_INSTALL_CLOSE_WINDOW = "First_Install_Close_Window";
  public static final String FIRST_INSTALL_START_DOWNLOAD = "First_Install_Start_Download";
  private static final String FIRST_INSTALL_SPONSORED_APPS_SELECTED = "sponsored_apps_selected";
  private static final String FIRST_INSTALL_NORMAL_APPS_SELECTED = "normal_apps_selected";

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public FirstInstallAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendPopupEvent() {
    analyticsManager.logEvent(null, FIRST_INSTALL_POP_UP, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendCloseWindowsEvent() {
    analyticsManager.logEvent(null, FIRST_INSTALL_CLOSE_WINDOW, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendStartDownloadEvent(String sponsored, String normal) {
    analyticsManager.logEvent(createStartDownloadMap(sponsored, normal),
        FIRST_INSTALL_START_DOWNLOAD, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createStartDownloadMap(String sponsored, String normal) {
    HashMap<String, Object> map = new HashMap<>();
    map.put(FIRST_INSTALL_SPONSORED_APPS_SELECTED, sponsored);
    map.put(FIRST_INSTALL_NORMAL_APPS_SELECTED, normal);
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
