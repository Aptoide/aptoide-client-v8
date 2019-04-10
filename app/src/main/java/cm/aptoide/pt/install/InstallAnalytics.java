package cm.aptoide.pt.install;

import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.AppContext;
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
  private static final String ACTION = "action";
  private static final String AB_TEST_GROUP = "ab_test_group";
  private static final String APP = "app";
  private static final String CAMPAIGN_ID = "campaign_id";
  private static final String EDITORS_CHOICE = "apps-group-editors-choice";
  private static final String FAIL = "FAIL";
  private static final String MAIN = "MAIN";
  private static final String MESSAGE = "message";
  private static final String MIGRATOR = "migrator";
  private static final String NETWORK = "network";
  private static final String NO_PREVIOUS_SCREEN_ERROR = "No_Previous_Screen";
  private static final String OBB = "obb";
  private static final String ORIGIN = "origin";
  private static final String PACKAGE = "package";
  private static final String PATCH = "PATCH";
  private static final String PHONE = "phone";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String PREVIOUS_TAG = "previous_tag";
  private static final String RESULT = "result";
  private static final String ROOT = "root";
  private static final String SETTINGS = "aptoide_settings";
  private static final String STATUS = "status";
  private static final String STORE = "store";
  private static final String SUCCESS = "SUCC";
  private static final String TELECO = "teleco";
  private static final String TYPE = "type";
  private static final String URL = "url";
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

  public void installCompleted(String packageName, int installingVersion, boolean isRoot,
      boolean aptoideSettings) {
    sendEvent(getKey(packageName, installingVersion, NOTIFICATION_APPLICATION_INSTALL));
    sendEvent(getKey(packageName, installingVersion, EDITORS_APPLICATION_INSTALL));
    sendEvent(getKey(packageName, installingVersion, APPLICATION_INSTALL));
    sendEvent(getKey(packageName, installingVersion, AppViewAnalytics.BONUS_MIGRATION_APPVIEW));
    sendInstallEvents(packageName, installingVersion, isRoot, aptoideSettings);
  }

  private void sendInstallEvents(String packageName, int installingVersion, boolean isPhoneRoot,
      boolean aptoideSettings) {
    InstallEvent installEvent =
        cache.get(getKey(packageName, installingVersion, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      sendInstallEvent(installEvent, isPhoneRoot, aptoideSettings, packageName, installingVersion);
    }
    InstallEvent applicationInstallEvent =
        cache.get(getKey(packageName, installingVersion, APPLICATION_INSTALL));
    if (applicationInstallEvent != null) {
      sendApplicationInstallEvent(applicationInstallEvent, isPhoneRoot, aptoideSettings,
          packageName, installingVersion);
    }
  }

  private void sendInstallEvent(InstallEvent installEvent, boolean isPhoneRoot,
      boolean aptoideSettings, String packageName, int installingVersion) {
    Map<String, Object> data = installEvent.getData();
    data.put(ROOT, createRoot(isPhoneRoot, aptoideSettings));
    data.put(RESULT, createResult());
    analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
        installEvent.getContext());
    cache.remove(getKey(packageName, installingVersion, INSTALL_EVENT_NAME));
  }

  private void sendApplicationInstallEvent(InstallEvent installEvent, boolean isPhoneRoot,
      boolean aptoideSettings, String packageName, int installingVersion) {
    Map<String, Object> data = installEvent.getData();
    data.put(ROOT, createRoot(isPhoneRoot, aptoideSettings));
    analyticsManager.logEvent(data, APPLICATION_INSTALL, installEvent.getAction(),
        installEvent.getContext());
    cache.remove(getKey(packageName, installingVersion, APPLICATION_INSTALL));
  }

  private Map<String, Object> createResult() {
    Map<String, Object> result = new HashMap<>();
    result.put(STATUS, SUCCESS);
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

  public void installStarted(String packageName, int versionCode, AnalyticsManager.Action action,
      AppContext context, Origin origin, boolean isMigration) {
    createApplicationInstallEvent(action, context, origin, packageName, versionCode, -1, null,
        Collections.emptyList(), isMigration);
    createInstallEvent(action, context, origin, packageName, versionCode, -1, null);
  }

  private void createApplicationInstallEvent(AnalyticsManager.Action action, AppContext context,
      Origin origin, String packageName, int installingVersion, int campaignId,
      String abTestingGroup, List<String> fragmentNameList, boolean isMigration) {
    Map<String, Object> data =
        getInstallEventsBaseBundle(origin, packageName, campaignId, abTestingGroup);
    data.put(MIGRATOR, isMigration);
    String applicationInstallEventName = "";
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreen = navigationTracker.getCurrentScreen();
    if (currentScreen.getTag() != null && currentScreen.getTag()
        .contains(EDITORS_CHOICE)) {
      applicationInstallEventName = EDITORS_APPLICATION_INSTALL;
    } else if (previousScreen == null) {
      if (!fragmentNameList.isEmpty()) {
        crashReport.log(NO_PREVIOUS_SCREEN_ERROR, fragmentNameList.toString());
      }
    } else if (currentScreen.getTag() != null && previousScreen.getFragment()
        .equals(DeepLinkManager.DEEPLINK_KEY)) {
      applicationInstallEventName = NOTIFICATION_APPLICATION_INSTALL;
    }
    if (!applicationInstallEventName.equals("")) {
      cache.put(getKey(packageName, installingVersion, applicationInstallEventName),
          new InstallEvent(data, applicationInstallEventName, context.name(), action));
    }
    cache.put(getKey(packageName, installingVersion, APPLICATION_INSTALL),
        new InstallEvent(data, applicationInstallEventName, context.name(), action));
  }

  private void createMigrationInstallEvent(AnalyticsManager.Action action, AppContext context,
      String packageName, int installingVersion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "install appc app");

    cache.put(getKey(packageName, installingVersion, AppViewAnalytics.BONUS_MIGRATION_APPVIEW),
        new InstallEvent(data, AppViewAnalytics.BONUS_MIGRATION_APPVIEW, context.name(), action));
  }

  public void installStarted(String packageName, int versionCode, AnalyticsManager.Action action,
      AppContext context, Origin origin, int campaignId, String abTestingGroup,
      boolean isMigration) {
    if (isMigration) createMigrationInstallEvent(action, context, packageName, versionCode);

    createApplicationInstallEvent(action, context, origin, packageName, versionCode, campaignId,
        abTestingGroup, Collections.emptyList(), isMigration);
    createInstallEvent(action, context, origin, packageName, versionCode, campaignId,
        abTestingGroup);
  }

  private void createInstallEvent(AnalyticsManager.Action action, AppContext context, Origin origin,
      String packageName, int installingVersion, int campaignId, String abTestingGroup) {
    Map<String, Object> data =
        getInstallEventsBaseBundle(origin, packageName, campaignId, abTestingGroup);
    cache.put(getKey(packageName, installingVersion, INSTALL_EVENT_NAME),
        new InstallEvent(data, INSTALL_EVENT_NAME, context.name(), action));
  }

  @NonNull private Map<String, Object> getInstallEventsBaseBundle(Origin origin, String packageName,
      int campaignId, String abTestingGroup) {
    ScreenTagHistory screenTagHistory = navigationTracker.getPreviousScreen();
    Map<String, Object> data = new HashMap<>();
    data.put(APP, createApp(packageName));
    data.put(NETWORK, AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    data.put(ORIGIN, origin);
    data.put(PREVIOUS_CONTEXT, screenTagHistory.getFragment());
    data.put(PREVIOUS_TAG, screenTagHistory.getTag());
    if (campaignId >= 0) {
      data.put(CAMPAIGN_ID, campaignId);
    }
    if (abTestingGroup != null) {
      data.put(AB_TEST_GROUP, abTestingGroup);
    }
    data.put(STORE, navigationTracker.getCurrentScreen()
        .getStore());
    data.put(TELECO, AptoideUtils.SystemU.getCarrierName(telephonyManager));
    return data;
  }

  private Map<String, Object> createRoot(boolean isPhoneRooted, boolean aptoideSettings) {
    Map<String, Object> root = new HashMap<>();
    root.put(PHONE, isPhoneRooted);
    root.put(SETTINGS, aptoideSettings);
    return root;
  }

  private Map<String, Object> createApp(String packageName) {
    Map<String, Object> app = new HashMap<>();
    app.put(PACKAGE, packageName);
    return app;
  }

  public void updateInstallEvents(int versionCode, String packageName, int fileType, String url) {
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    InstallEvent applicationInstallEvent =
        cache.get(getKey(packageName, versionCode, APPLICATION_INSTALL));
    if (installEvent != null) {
      updateApp(versionCode, packageName, fileType, url, installEvent, INSTALL_EVENT_NAME);
      updateObb(versionCode, packageName, fileType, url, installEvent, INSTALL_EVENT_NAME);
    }
    if (applicationInstallEvent != null) {
      updateApp(versionCode, packageName, fileType, url, applicationInstallEvent,
          APPLICATION_INSTALL);
      updateObb(versionCode, packageName, fileType, url, applicationInstallEvent,
          APPLICATION_INSTALL);
    }
  }

  private void updateObb(int versionCode, String packageName, int fileType, String url,
      InstallEvent installEvent, String installEventName) {
    if (fileType == 1 || fileType == 2) {
      Map<String, Object> data = installEvent.getData();
      List<Map<String, Object>> obb = (List<Map<String, Object>>) data.get("obb");
      if (obb == null) {
        obb = new ArrayList<>();
      }
      obb.add(createObbData(fileType, url));
      data.put(OBB, obb);
      cache.put(getKey(packageName, versionCode, installEventName),
          new InstallEvent(data, installEvent.getEventName(), installEvent.getContext(),
              installEvent.getAction()));
    }
  }

  private Map<String, Object> createObbData(int fileType, String url) {
    Map<String, Object> obb = new HashMap<>();
    if (fileType == 1) {
      obb.put(TYPE, MAIN);
    } else if (fileType == 2) {
      obb.put(TYPE, PATCH);
    }
    obb.put(URL, url);
    return obb;
  }

  private void updateApp(int versionCode, String packageName, int fileType, String url,
      InstallEvent installEvent, String installEventName) {
    if (fileType == 0) {
      Map<String, Object> data = installEvent.getData();
      Map<String, Object> app = (Map<String, Object>) data.get(APP);
      app.put(URL, url);
      data.put(APP, app);
      cache.put(getKey(packageName, versionCode, installEventName),
          new InstallEvent(data, installEventName, installEvent.getContext(),
              installEvent.getAction()));
    }
  }

  public void logInstallErrorEvent(String packageName, int versionCode, Exception exception,
      boolean isPhoneRoot, boolean aptoideSettings) {
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      Map<String, Object> data = installEvent.getData();
      data.put(ROOT, createRoot(isPhoneRoot, aptoideSettings));
      data.put(RESULT, createResult(exception));
      analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    }
  }

  private Map<String, Object> createResult(Exception exception) {
    Map<String, Object> result = new HashMap<>();
    result.put(STATUS, FAIL);
    result.put(TYPE, exception.getClass()
        .getSimpleName());
    result.put(MESSAGE, exception.getMessage());
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
