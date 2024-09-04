package cm.aptoide.pt.install;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.DownloadAnalytics;
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
  public static final String CLICK_ON_INSTALL = "click_on_install_button";
  public static final String RAKAM_INSTALL_EVENT = "install";
  private static final String UPDATE_TO_APPC = "UPDATE TO APPC";
  private static final int MIGRATION_UNINSTALL_KEY = 8726;
  private static final String ACTION = "action";
  private static final String AB_TEST_GROUP = "ab_test_group";
  private static final String APP = "app";
  private static final String APPC = "appc";
  private static final String APP_BUNDLE = "app_bundle";
  private static final String APP_MIGRATION = "app_migration";
  private static final String APP_APPC = "app_appc";
  private static final String APP_AAB = "app_aab";
  private static final String CAMPAIGN_ID = "campaign_id";
  private static final String EDITORS_CHOICE = "apps-group-editors-choice";
  private static final String FAIL = "FAIL";
  private static final String CANCEL = "CANCEL";
  private static final String CONTEXT = "context";
  private static final String MAIN = "MAIN";
  private static final String MESSAGE = "message";
  private static final String MIGRATOR = "migrator";
  private static final String NETWORK = "network";
  private static final String NO_PREVIOUS_SCREEN_ERROR = "No_Previous_Screen";
  private static final String OBB = "obb";
  private static final String ORIGIN = "origin";
  private static final String PACKAGE = "package";
  private static final String PACKAGE_NAME = "package_name";
  private static final String PATCH = "PATCH";
  private static final String PHONE = "phone";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String TAG = "tag";
  private static final String RESULT = "result";
  private static final String ROOT = "root";
  private static final String SETTINGS = "aptoide_settings";
  private static final String STATUS = "status";
  private static final String STORE = "store";
  private static final String SUCCESS = "SUCC";
  private static final String TELECO = "teleco";
  private static final String TRUSTED_BADGE = "trusted_badge";
  private static final String TYPE = "type";
  private static final String URL = "url";
  private static final String ERROR_TYPE = "error_type";
  private static final String ERROR_MESSAGE = "error_message";
  private static final String IS_APKFY = "apkfy_app_install";
  private static final String MIUI_AAB_FIX = "miui_aab_fix";
  private static final String APP_OBB = "app_obb";
  private static final String APP_AAB_INSTALL_TIME = "app_aab_install_time";
  private static final String APP_IN_CATAPPULT = "app_in_catappult";
  private static final String APP_VERSION_CODE = "app_version_code";
  public static final String GAMES_CATEGORY = "games";
  private static final String APP_IS_GAME = "app_is_game";

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
    sendEvent(getKey(packageName, installingVersion, RAKAM_INSTALL_EVENT));
    sendInstallEvents(packageName, installingVersion, isRoot, aptoideSettings);
  }

  public void uninstallCompleted(String packageName) {
    sendEvent(
        getKey(packageName, MIGRATION_UNINSTALL_KEY, AppViewAnalytics.BONUS_MIGRATION_APPVIEW));
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
      DownloadAnalytics.AppContext context, Origin origin, boolean isMigration, boolean hasAppc,
      boolean isAppBundle, String trustedBadge, String storeName, boolean hasObbs,
      String splitTypes, boolean isInCatappult, String appCategory) {

    createRakamInstallEvent(versionCode, packageName, origin.toString(),
        isMigration, isAppBundle, hasAppc, trustedBadge, storeName, context, false, hasObbs,
        splitTypes, isInCatappult, versionCode, appCategory);
    createApplicationInstallEvent(action, context, origin, packageName, versionCode, -1, null,
        Collections.emptyList(), isMigration, hasAppc, isAppBundle, false);
    createInstallEvent(action, context, origin, packageName, versionCode, -1, null, isMigration,
        hasAppc, isAppBundle, false);
  }

  private void createRakamInstallEvent(int installingVersion, String packageName, String action,
      boolean isMigration, boolean isAppBundle, boolean hasAppc,
      String trustedBadge, String storeName, DownloadAnalytics.AppContext appContext,
      boolean isApkfy, boolean hasObbs, String splitTypes, boolean isInCatappult, int versionCode,
      String appCategory) {
    String previousContext = navigationTracker.getPreviousViewName();
    String context = navigationTracker.getCurrentViewName();
    String tag_ =
        navigationTracker.getCurrentScreen() != null ? navigationTracker.getCurrentScreen()
            .getTag() : "";

    HashMap<String, Object> result = new HashMap<>();
    result.put(CONTEXT, context);
    result.put(ACTION, action.toLowerCase());
    result.put(PACKAGE_NAME, packageName);
    result.put(PREVIOUS_CONTEXT, previousContext);
    result.put(APP_MIGRATION, isMigration);
    result.put(APP_APPC, hasAppc);
    result.put(APP_AAB, isAppBundle);
    result.put(APP_OBB, hasObbs);
    result.put(STATUS, "success");
    result.put(IS_APKFY, isApkfy);
    result.put(APP_AAB_INSTALL_TIME, splitTypes);
    result.put(APP_VERSION_CODE, versionCode);
    result.put(APP_IN_CATAPPULT, isInCatappult);
    if (!appCategory.isEmpty()) {
      result.put(APP_IS_GAME, appCategory.equals(GAMES_CATEGORY));
    }

    if (trustedBadge != null) result.put(TRUSTED_BADGE, trustedBadge.toLowerCase());
    if (!tag_.isEmpty()) result.put(TAG, tag_);
    result.put(STORE, storeName);

    cache.put(getKey(packageName, installingVersion, RAKAM_INSTALL_EVENT),
        new InstallEvent(result, RAKAM_INSTALL_EVENT, appContext.name(),
            AnalyticsManager.Action.CLICK));
  }

  private void createApplicationInstallEvent(AnalyticsManager.Action action,
      DownloadAnalytics.AppContext context, Origin origin, String packageName,
      int installingVersion, int campaignId, String abTestingGroup, List<String> fragmentNameList,
      boolean isMigration, boolean hasAppc, boolean isAppBundle, boolean isApkfy) {
    Map<String, Object> data =
        getApplicationInstallEventsBaseBundle(packageName, campaignId, abTestingGroup, hasAppc,
            isAppBundle, navigationTracker.getViewName(true));
    data.put(MIGRATOR, isMigration);
    if (isMigration) {
      data.put(ORIGIN, Origin.UPDATE_TO_APPC);
    } else {
      data.put(ORIGIN, origin);
    }
    data.put(IS_APKFY, isApkfy);

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
        new InstallEvent(data, APPLICATION_INSTALL, context.name(), action));
  }

  private void createMigrationInstallEvent(AnalyticsManager.Action action,
      DownloadAnalytics.AppContext context, String packageName, int installingVersion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "install appc app");

    cache.put(getKey(packageName, installingVersion, AppViewAnalytics.BONUS_MIGRATION_APPVIEW),
        new InstallEvent(data, AppViewAnalytics.BONUS_MIGRATION_APPVIEW, context.name(), action));
  }

  public void installStarted(String packageName, int versionCode, AnalyticsManager.Action action,
      DownloadAnalytics.AppContext context, Origin origin, int campaignId, String abTestingGroup,
      boolean isMigration, boolean hasAppc, boolean isAppBundle, String trustedBadge,
      String storeName, boolean isApkfy, boolean hasObbs, String splitTypes,
      boolean isInCatappult, String appCategory) {

    createRakamInstallEvent(versionCode, packageName, origin.toString(),
        isMigration, isAppBundle, hasAppc, trustedBadge, storeName, context, isApkfy, hasObbs,
        splitTypes, isInCatappult, versionCode, appCategory);

    if (isMigration) createMigrationInstallEvent(action, context, packageName, versionCode);

    createApplicationInstallEvent(action, context, origin, packageName, versionCode, campaignId,
        abTestingGroup, Collections.emptyList(), isMigration, hasAppc, isAppBundle, isApkfy);
    createInstallEvent(action, context, origin, packageName, versionCode, campaignId,
        abTestingGroup, isMigration, hasAppc, isAppBundle, isApkfy);
  }

  public void uninstallStarted(String packageName, AnalyticsManager.Action action,
      DownloadAnalytics.AppContext context) {
    Map<String, Object> data = new HashMap<>();

    data.put(ACTION, "uninstall");

    cache.put(
        getKey(packageName, MIGRATION_UNINSTALL_KEY, AppViewAnalytics.BONUS_MIGRATION_APPVIEW),
        new InstallEvent(data, AppViewAnalytics.BONUS_MIGRATION_APPVIEW, context.name(), action));
  }

  private void createInstallEvent(AnalyticsManager.Action action,
      DownloadAnalytics.AppContext context, Origin origin, String packageName,
      int installingVersion, int campaignId, String abTestingGroup, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, boolean isApkfy) {
    Map<String, Object> data =
        getInstallEventsBaseBundle(packageName, campaignId, abTestingGroup, hasAppc, isMigration,
            isAppBundle);
    if (isMigration) {
      data.put(ORIGIN, UPDATE_TO_APPC);
    } else {
      data.put(ORIGIN, origin);
    }
    data.put(IS_APKFY, isApkfy);
    cache.put(getKey(packageName, installingVersion, INSTALL_EVENT_NAME),
        new InstallEvent(data, INSTALL_EVENT_NAME, context.name(), action));
  }

  @NonNull
  private Map<String, Object> getInstallEventsBaseBundle(String packageName, int campaignId,
      String abTestingGroup, boolean hasAppc, boolean isMigration, boolean isAppBundle) {
    ScreenTagHistory previousScreenTagHistory = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreenTagHistory = navigationTracker.getCurrentScreen();
    Map<String, Object> data = new HashMap<>();
    data.put(APP, createApp(packageName, hasAppc, isMigration, isAppBundle));
    data.put(NETWORK, AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    data.put(PREVIOUS_CONTEXT, previousScreenTagHistory.getFragment());
    data.put(TAG, currentScreenTagHistory.getTag());
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

  @NonNull private Map<String, Object> getApplicationInstallEventsBaseBundle(String packageName,
      int campaignId, String abTestingGroup, boolean hasAppc, boolean isAppBundle, String context) {
    ScreenTagHistory previousScreenTagHistory = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreenTagHistory = navigationTracker.getCurrentScreen();
    Map<String, Object> data = new HashMap<>();
    data.put(PACKAGE, packageName);
    data.put(APPC, hasAppc);
    data.put(APP_BUNDLE, isAppBundle);
    data.put(NETWORK, AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    data.put(PREVIOUS_CONTEXT, previousScreenTagHistory.getFragment());
    data.put(TAG, currentScreenTagHistory.getTag());
    if (campaignId >= 0) {
      data.put(CAMPAIGN_ID, campaignId);
    }
    if (abTestingGroup != null) {
      data.put(AB_TEST_GROUP, abTestingGroup);
    }
    data.put(STORE, navigationTracker.getCurrentScreen()
        .getStore());
    data.put(TELECO, AptoideUtils.SystemU.getCarrierName(telephonyManager));
    data.put(CONTEXT, context);
    return data;
  }

  private Map<String, Object> createRoot(boolean isPhoneRooted, boolean aptoideSettings) {
    Map<String, Object> root = new HashMap<>();
    root.put(PHONE, isPhoneRooted);
    root.put(SETTINGS, aptoideSettings);
    return root;
  }

  private Map<String, Object> createApp(String packageName, boolean hasAppc, boolean isMigration,
      boolean isAppBundle) {
    Map<String, Object> app = new HashMap<>();
    app.put(PACKAGE, packageName);
    app.put(APPC, hasAppc);
    app.put(MIGRATOR, isMigration);
    app.put(APP_BUNDLE, isAppBundle);
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
      applicationInstallUpdateApp(versionCode, packageName, fileType, url, applicationInstallEvent,
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

  private void applicationInstallUpdateApp(int versionCode, String packageName, int fileType,
      String url, InstallEvent installEvent, String installEventName) {
    if (fileType == 0) {
      Map<String, Object> data = installEvent.getData();
      data.put(URL, url);
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
      data.put(RESULT, createFailResult(exception));
      analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    }
  }

  private Map<String, Object> createFailResult(Exception exception) {
    Map<String, Object> result = new HashMap<>();
    result.put(STATUS, FAIL);
    result.put(TYPE, exception.getClass()
        .getSimpleName());
    result.put(MESSAGE, exception.getMessage());
    return result;
  }

  public void logInstallCancelEvent(String packageName, int versionCode) {
    sendRakamInstallCanceledEvent(packageName, versionCode);
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    if (installEvent != null) {
      Map<String, Object> data = installEvent.getData();
      data.put(RESULT, createCancelResult());
      analyticsManager.logEvent(data, INSTALL_EVENT_NAME, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, versionCode, INSTALL_EVENT_NAME));
    }
  }

  private void sendRakamInstallCanceledEvent(String packageName, int versionCode) {
    InstallEvent installEvent = cache.get(getKey(packageName, versionCode, RAKAM_INSTALL_EVENT));
    if (installEvent != null) {
      Map<String, Object> data = installEvent.getData();
      data.put(STATUS, "fail");
      data.put(ERROR_TYPE, "canceled");
      data.put(ERROR_MESSAGE, "The install was canceled");
      analyticsManager.logEvent(data, RAKAM_INSTALL_EVENT, installEvent.getAction(),
          installEvent.getContext());
      cache.remove(getKey(packageName, versionCode, RAKAM_INSTALL_EVENT));
    }
  }

  public void clickOnInstallEvent(String packageName, String type, boolean hasSplits,
      boolean hasBilling, boolean isMigration, String rank, String origin,
      String store, boolean isApkfy, boolean hasObb, boolean isInCatappult, String appCategory) {
    String context = navigationTracker.getCurrentViewName();

    Map<String, Object> eventMap =
        createInstallClickEventMap(packageName, type, hasSplits, hasBilling, isMigration, rank,
            origin, store, context, isApkfy, hasObb, isInCatappult, appCategory);

    analyticsManager.logEvent(eventMap, CLICK_ON_INSTALL, AnalyticsManager.Action.CLICK, context);
  }

  private Map<String, Object> createInstallClickEventMap(String packageName, String type,
      boolean hasSplits, boolean hasBilling, boolean isMigration, String rank,
      String origin, String store, String context, boolean isApkfy, boolean hasObb,
      boolean isInCatappult, String appCategory) {
    String previousContext = navigationTracker.getPreviousViewName();

    Map<String, Object> result = new HashMap<>();
    result.put(CONTEXT, context);
    result.put(ACTION, type.toLowerCase());
    result.put(PACKAGE_NAME, packageName);
    result.put(PREVIOUS_CONTEXT, previousContext);
    result.put(APP_MIGRATION, isMigration);
    result.put(APP_APPC, hasBilling);
    result.put(APP_AAB, hasSplits);
    result.put(IS_APKFY, isApkfy);
    result.put(APP_OBB, hasObb);
    result.put(APP_IN_CATAPPULT, isInCatappult);
    if (!appCategory.isEmpty()) {
      result.put(APP_IS_GAME, appCategory.equals(GAMES_CATEGORY));
    }
    if (rank != null) result.put(TRUSTED_BADGE, rank.toLowerCase());

    if (origin != null) result.put(TAG, origin);
    result.put(STORE, store);
    return result;
  }

  private Map<String, Object> createCancelResult() {
    Map<String, Object> result = new HashMap<>();
    result.put(STATUS, CANCEL);
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
