package cm.aptoide.pt.install;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.DeepLinkManager;
import java.util.ArrayList;
import java.util.Collections;
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
  public static final String INSTALL_EVENT_NAME = "INSTALL";
  private static final String TYPE = "Type";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String REPLACED = "Replaced";
  private static final String NO_PREVIOUS_SCREEN_ERROR = "No_Previous_Screen";
  private final CrashReport crashReport;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final Map<String, InstallEvent> cache;
  private final ConnectivityManager connectivityManager;
  private final TelephonyManager telephonyManager;

  public InstallAnalytics(CrashReport crashReport, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, Map<String, InstallEvent> cache,
      ConnectivityManager connectivityManager, TelephonyManager telephonyManager) {
    this.crashReport = crashReport;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.cache = cache;
    this.connectivityManager = connectivityManager;
    this.telephonyManager = telephonyManager;
  }

  public void sendReplacedEvent(String packageName) {
    Map<String, Object> data = new HashMap<>();
    data.put(TYPE, REPLACED);
    data.put(PACKAGE_NAME, packageName);
    analyticsManager.logEvent(data, APPLICATION_INSTALL, AnalyticsManager.Action.AUTO,
        getViewName(true));
  }

  private void installStarted(String packageName, int installingVersion, InstallType installType,
      List<String> fragmentNameList) {
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreen = navigationTracker.getCurrentScreen();
    if (currentScreen.getTag() != null && currentScreen.getTag()
        .contains("apps-group-editors-choice")) {
      Map<String, Object> data = new HashMap<>();
      data.put("package_name", packageName);
      data.put("type", installType.name());
      cache.put(getKey(packageName, installingVersion, EDITORS_APPLICATION_INSTALL),
          new InstallEvent(data, EDITORS_APPLICATION_INSTALL, currentScreen.getFragment(),
              AnalyticsManager.Action.INSTALL));
    } else if (previousScreen == null) {
      if (!fragmentNameList.isEmpty()) {
        crashReport.log(NO_PREVIOUS_SCREEN_ERROR, fragmentNameList.toString());
      }
    } else if (currentScreen.getTag() != null && previousScreen.getFragment()
        .equals(DeepLinkManager.DEEPLINK_KEY)) {
      Map<String, Object> data = new HashMap<>();
      data.put("package_name", packageName);
      data.put("type", installType.name());
      cache.put(getKey(packageName, installingVersion, NOTIFICATION_APPLICATION_INSTALL),
          new InstallEvent(data, NOTIFICATION_APPLICATION_INSTALL, currentScreen.getFragment(),
              AnalyticsManager.Action.INSTALL));
    }
  }

  public void installCompleted(String packageName, int installingVersion, boolean isRoot,
      boolean aptoideSettings) {
    sendEvent(getKey(packageName, installingVersion, NOTIFICATION_APPLICATION_INSTALL));
    sendEvent(getKey(packageName, installingVersion, EDITORS_APPLICATION_INSTALL));
    sendInstallEvent(packageName, installingVersion, isRoot, aptoideSettings);
  }

  private void sendInstallEvent(String packageName, int installingVersion, boolean isPhoneRoot,
      boolean aptoideSettings) {
    InstallEvent installEvent =
        cache.get(getKey(packageName, installingVersion, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      Map<String, Object> data = installEvent.getData();
      data.put("root", createRoot(isPhoneRoot, aptoideSettings));
      data.put("result", createResult());
      analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, installingVersion, INSTALL_EVENT_NAME));
    }
  }

  private Map<String, Object> createResult() {
    Map<String, Object> result = new HashMap<>();
    result.put("status", "SUCC");
    return result;
  }

  private String getKey(String packageName, int installingVersion, String eventName) {
    return packageName + installingVersion + eventName;
  }

  private void sendEvent(String key) {
    InstallEvent installEvent = cache.get(key);
    if (installEvent != null) {
      analyticsManager.logEvent(installEvent.getData(), installEvent.getEventName(),
          installEvent.getAction(), installEvent.getContext());
    }
    cache.remove(key);
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void installStarted(String packageName, int versionCode, InstallType update,
      AnalyticsManager.Action action, AppContext context,
      Origin origin) {
    installStarted(packageName, versionCode, update, Collections.emptyList());
    createInstallEvent(action, context, origin, packageName, versionCode, -1, null);
  }

  public void installStarted(String packageName, int versionCode, InstallType update,
      AnalyticsManager.Action action, AppContext context,
      Origin origin, int campaignId, String abTestingGroup) {
    installStarted(packageName, versionCode, update, Collections.emptyList());
    createInstallEvent(action, context, origin, packageName, versionCode, campaignId,
        abTestingGroup);
  }

  private void createInstallEvent(AnalyticsManager.Action action,
      AppContext context, Origin origin,
      String packageName, int installingVersion, int campaignId, String abTestingGroup) {

    Map<String, Object> data = new HashMap<>();
    data.put("app", createApp(packageName));
    data.put("network", AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    data.put("origin", origin);
    data.put("previous_context", navigationTracker.getCurrentScreen()
        .getFragment());
    data.put("previous_tag", navigationTracker.getCurrentScreen()
        .getTag());
    if (campaignId >= 0) {
      data.put("campaign_id", campaignId);
    }
    if (abTestingGroup != null) {
      data.put("ab_test_group", abTestingGroup);
    }
    data.put("store", navigationTracker.getCurrentScreen()
        .getStore());
    data.put("teleco", AptoideUtils.SystemU.getCarrierName(telephonyManager));
    cache.put(getKey(packageName, installingVersion, INSTALL_EVENT_NAME),
        new InstallEvent(data, INSTALL_EVENT_NAME, context.name(), action));
  }

  private Map<String, Object> createRoot(boolean isPhoneRooted, boolean aptoideSettings) {
    Map<String, Object> root = new HashMap<>();
    root.put("phone", isPhoneRooted);
    root.put("aptoide_settings", aptoideSettings);
    return root;
  }

  private Map<String, Object> createApp(String packageName) {
    Map<String, Object> app = new HashMap<>();
    app.put("package", packageName);
    return app;
  }

  public void installStarted(String packageName, int versionCode, InstallType installType,
      AnalyticsManager.Action action, AppContext context,
      Origin origin, List<String> fragments) {
    installStarted(packageName, versionCode, installType, fragments);
    createInstallEvent(action, context, origin, packageName, versionCode, -1, null);
  }

  public void updateInstallEvent(int versionCode, String packageName, int fileType, String url) {
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      updateApp(versionCode, packageName, fileType, url, installEvent);
      updateObb(versionCode, packageName, fileType, url, installEvent);
    }
  }

  private void updateObb(int versionCode, String packageName, int fileType, String url,
      InstallEvent installEvent) {
    if (fileType == 1 || fileType == 2) {
      Map<String, Object> data = installEvent.getData();
      List<Map<String, Object>> obb = (List<Map<String, Object>>) data.get("obb");
      if (obb == null) {
        obb = new ArrayList<>();
      }
      obb.add(createObbData(fileType, url));
      data.put("obb", obb);
      cache.put(getKey(packageName, versionCode, INSTALL_EVENT_NAME),
          new InstallEvent(data, installEvent.getEventName(), installEvent.getContext(),
              installEvent.getAction()));
    }
  }

  private Map<String, Object> createObbData(int fileType, String url) {
    Map<String, Object> obb = new HashMap<>();
    if (fileType == 1) {
      obb.put("type", "MAIN");
    } else if (fileType == 2) {
      obb.put("type", "PATCH");
    }
    obb.put("url", url);
    return obb;
  }

  private void updateApp(int versionCode, String packageName, int fileType, String url,
      InstallEvent installEvent) {
    if (fileType == 0) {
      Map<String, Object> data = installEvent.getData();
      Map<String, Object> app = (Map<String, Object>) data.get("app");
      app.put("url", url);
      data.put("app", app);
      cache.put(getKey(packageName, versionCode, INSTALL_EVENT_NAME),
          new InstallEvent(data, INSTALL_EVENT_NAME, installEvent.getContext(),
              installEvent.getAction()));
    }
  }

  public void logInstallErrorEvent(String packageName, int versionCode, Exception exception,
      boolean isPhoneRoot, boolean aptoideSettings) {
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      Map<String, Object> data = installEvent.getData();
      data.put("root", createRoot(isPhoneRoot, aptoideSettings));
      data.put("result", createResult(exception));
      analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    }
  }

  private Map<String, Object> createResult(Exception exception) {
    Map<String, Object> result = new HashMap<>();
    result.put("status", "FAIL");
    result.put("type", exception.getClass()
        .getSimpleName());
    result.put("message", exception.getMessage());
    return result;
  }

  private static class InstallEvent {
    private final Map<String, Object> data;
    private final String eventName;
    private final String context;
    private final AnalyticsManager.Action action;

    private InstallEvent(Map<String, Object> data, String eventName, String context,
        AnalyticsManager.Action action) {
      this.data = data;
      this.eventName = eventName;
      this.context = context;
      this.action = action;
    }

    public Map<String, Object> getData() {
      return data;
    }

    public String getEventName() {
      return eventName;
    }

    public String getContext() {
      return context;
    }

    public AnalyticsManager.Action getAction() {
      return action;
    }
  }
}
