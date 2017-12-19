package cm.aptoide.pt.install;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.DeepLinkManager;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pedroribeiro on 19/06/17.
 */

public class InstallAnalytics {

  public static final String NOTIFICATION_APPLICATION_INSTALL =
      "Aptoide_Push_Notification_Application_Install";
  private static final String APPLICATION_INSTALL = "Application Install";
  private static final String EDITORS_APPLICATION_INSTALL = "Editors_Choice_Application_Install";
  private static final String TYPE = "Type";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private static final String INSTALLED = "Installed";
  private static final String REPLACED = "Replaced";
  private static final String NO_PREVIOUS_SCREEN_ERROR = "No_Previous_Screen";
  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private CrashReport crashReport;

  public InstallAnalytics(Analytics analytics, AppEventsLogger facebook, CrashReport crashReport) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.crashReport = crashReport;
  }

  public void sendInstalledEvent(String packageName) {
    analytics.sendEvent(
        new FacebookEvent(facebook, APPLICATION_INSTALL, createInstalledBundleData(packageName)));
    analytics.sendEvent(new FlurryEvent(APPLICATION_INSTALL, createInstalledMapData(packageName)));
  }

  public void sendRepalcedEvent(String packageName) {
    analytics.sendEvent(
        new FacebookEvent(facebook, APPLICATION_INSTALL, createReplacedBundleData(packageName)));
    analytics.sendEvent(new FlurryEvent(APPLICATION_INSTALL, createReplacedMapData(packageName)));
  }

  private Bundle createInstalledBundleData(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, INSTALLED);
    bundle.putString(PACKAGE_NAME, packageName);
    return bundle;
  }

  private HashMap<String, String> createInstalledMapData(String packageName) {
    HashMap<String, String> map = new HashMap<>();
    map.put(TYPE, INSTALLED);
    map.put(PACKAGE_NAME, packageName);
    return map;
  }

  private Bundle createReplacedBundleData(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, REPLACED);
    bundle.putString(PACKAGE_NAME, packageName);
    return bundle;
  }

  private HashMap<String, String> createReplacedMapData(String packageName) {
    HashMap<String, String> map = new HashMap<>();
    map.put(TYPE, REPLACED);
    map.put(PACKAGE_NAME, packageName);
    return map;
  }

  public void installStarted(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String packageName, int installingVersion, InstallType installType,
      List<String> fragmentNameList) {
    if (currentScreen.getTag() != null && currentScreen.getTag()
        .contains("apps-group-editors-choice")) {
      Bundle data = new Bundle();
      data.putString("package_name", packageName);
      data.putString("type", installType.name());
      analytics.save(packageName + installingVersion,
          new FacebookEvent(facebook, EDITORS_APPLICATION_INSTALL, data));
    } else if (previousScreen == null) {
      if (!fragmentNameList.isEmpty()) {
        crashReport.log(NO_PREVIOUS_SCREEN_ERROR, fragmentNameList.toString());
      }
    } else if (currentScreen.getTag() != null && previousScreen.getFragment()
        .equals(DeepLinkManager.DEEPLINK_KEY)) {
      Bundle data = new Bundle();
      data.putString("package_name", packageName);
      data.putString("type", installType.name());
      analytics.save(packageName + installingVersion,
          new FacebookEvent(facebook, NOTIFICATION_APPLICATION_INSTALL, data));
    }
  }

  public void installCompleted(String packageName, int installingVersion) {
    FacebookEvent event = analytics.getFacebookEvent(packageName + installingVersion);
    if (event != null) {
      analytics.sendEvent(event);
    }
  }

  public enum InstallType {
    INSTALL, UPDATE, DOWNGRADE
  }
}
