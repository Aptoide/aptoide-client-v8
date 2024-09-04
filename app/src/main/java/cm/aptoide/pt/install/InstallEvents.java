package cm.aptoide.pt.install;

import android.content.SharedPreferences;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.packageinstaller.InstallStatus;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.root.RootAvailabilityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/06/2017.
 */

public class InstallEvents implements InstallerAnalytics {
  public static final String ROOT_V2_COMPLETE = "Root_v2_Complete";
  public static final String ROOT_V2_START = "Root_v2_Start";
  public static final String IS_INSTALLATION_TYPE_EVENT_NAME = "INSTALLATION_TYPE";
  private static final String CONCAT = "CONCAT";
  private static final String IS_ROOT = "IS_ROOT";
  private static final String SETTING_ROOT = "SETTING_ROOT";
  private static final String INSTALLFABRICCONTEXT = "Install_Fabric_Event";
  private final InstallAnalytics installAnalytics;
  private final AnalyticsManager analyticsManager;
  private final SharedPreferences sharedPreferences;
  private final RootAvailabilityManager rootAvailabilityManager;
  private final NavigationTracker navigationTracker;

  public InstallEvents(AnalyticsManager analyticsManager, InstallAnalytics installAnalytics,
      SharedPreferences sharedPreferences, RootAvailabilityManager rootAvailabilityManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.installAnalytics = installAnalytics;
    this.sharedPreferences = sharedPreferences;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.navigationTracker = navigationTracker;
  }

  @Override public void rootInstallCompleted(int exitcode) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "success");
    attributes.put("Exit_Code", String.valueOf(exitcode));
    analyticsManager.logEvent(attributes, ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void rootInstallTimeout() {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "timeout");
    analyticsManager.logEvent(attributes, ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void rootInstallFail(Exception e) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "fail");
    attributes.put("Error", e.getMessage());
    analyticsManager.logEvent(attributes, ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void rootInstallCancelled() {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "cancel");
    analyticsManager.logEvent(attributes, ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void rootInstallStart() {
    analyticsManager.logEvent(null, ROOT_V2_START, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void installationType(boolean isRootAllowed, boolean isRoot) {
    Map<String, Object> map = new HashMap<>();
    map.put(IS_ROOT, String.valueOf(isRoot));
    map.put(SETTING_ROOT, String.valueOf(isRootAllowed));
    map.put(CONCAT, isRootAllowed + "_" + isRoot);
    analyticsManager.logEvent(map, IS_INSTALLATION_TYPE_EVENT_NAME, AnalyticsManager.Action.ROOT,
        INSTALLFABRICCONTEXT);
  }

  @Override public void logInstallErrorEvent(String packageName, int versionCode, Exception e) {
    installAnalytics.logInstallErrorEvent(packageName, versionCode, e,
        rootAvailabilityManager.isRootAvailable()
            .toBlocking()
            .value(), ManagerPreferences.allowRootInstallation(sharedPreferences));
  }

  @Override public void logInstallCancelEvent(String packageName, int versionCode) {
    installAnalytics.logInstallCancelEvent(packageName, versionCode);
  }
}
