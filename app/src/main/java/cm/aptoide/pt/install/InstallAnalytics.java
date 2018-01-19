package cm.aptoide.pt.install;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.analytics.analytics.Event;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.DeepLinkManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedroribeiro on 19/06/17.
 */

public class InstallAnalytics {

  public static final String NOTIFICATION_APPLICATION_INSTALL =
      "Aptoide_Push_Notification_Application_Install";
  public static final String APPLICATION_INSTALL = "Application Install";
  public static final String EDITORS_APPLICATION_INSTALL = "Editors_Choice_Application_Install";
  private static final String TYPE = "Type";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String REPLACED = "Replaced";
  private static final String NO_PREVIOUS_SCREEN_ERROR = "No_Previous_Screen";
  private CrashReport crashReport;
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public InstallAnalytics(CrashReport crashReport,
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.crashReport = crashReport;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendReplacedEvent(String packageName) {
    Map<String, Object> data = new HashMap<>();
    data.put(TYPE,REPLACED);
    data.put(PACKAGE_NAME,packageName);
    analyticsManager.logEvent(data, APPLICATION_INSTALL,AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void installStarted(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String packageName, int installingVersion, InstallType installType,
      List<String> fragmentNameList) {
    if (currentScreen.getTag() != null && currentScreen.getTag()
        .contains("apps-group-editors-choice")) {
      Map<String, Object> data = new HashMap<>();
      data.put("package_name", packageName);
      data.put("type", installType.name());
      analyticsManager.save(packageName + installingVersion,
          new Event(EDITORS_APPLICATION_INSTALL, data, AnalyticsManager.Action.INSTALL,getViewName(true)));
    } else if (previousScreen == null) {
      if (!fragmentNameList.isEmpty()) {
        crashReport.log(NO_PREVIOUS_SCREEN_ERROR, fragmentNameList.toString());
      }
    } else if (currentScreen.getTag() != null && previousScreen.getFragment()
        .equals(DeepLinkManager.DEEPLINK_KEY)) {
      Map<String, Object> data = new HashMap<>();
      data.put("package_name", packageName);
      data.put("type", installType.name());
      analyticsManager.save(packageName + installingVersion,
          new Event(NOTIFICATION_APPLICATION_INSTALL, data, AnalyticsManager.Action.INSTALL,getViewName(true)));
    }
  }

  public void installCompleted(String packageName, int installingVersion) {
    Event event = analyticsManager.getEvent(packageName + installingVersion);
    if (event != null) {
      analyticsManager.logEvent(event.getData(),event.getEventName(),event.getAction(),event.getContext());
    }
  }

  public enum InstallType {
    INSTALL, UPDATE, DOWNGRADE
  }

  private String getViewName(boolean isCurrent){
    String viewName = "";
    if(isCurrent){
      viewName = navigationTracker.getCurrentViewName();
    }
    else{
      viewName = navigationTracker.getPreviousViewName();
    }
    if(viewName.equals("")) {
      return "InstallAnalytics"; //Default value, shouldn't get here
    }
    return viewName;
  }
}
