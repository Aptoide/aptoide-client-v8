package cm.aptoide.pt.v8engine.install;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;

/**
 * Created by pedroribeiro on 19/06/17.
 */

public class InstallAnalytics {

  private static final String APPLICATION_INSTALL = "Application Install";
  private static final String TYPE = "Type";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private static final String INSTALLED = "Installed";
  private static final String REPLACED = "Replaced";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public InstallAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void installed(String packageName) {
    analytics.sendEvent(
        new FacebookEvent(facebook, APPLICATION_INSTALL, createInstalledBundleData(packageName)));
    analytics.sendEvent(new FlurryEvent(APPLICATION_INSTALL, createInstalledMapData(packageName)));
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

  public void replaced(String packageName) {
    analytics.sendEvent(
        new FacebookEvent(facebook, APPLICATION_INSTALL, createReplacedBundleData(packageName)));
    analytics.sendEvent(new FlurryEvent(APPLICATION_INSTALL, createReplacedMapData(packageName)));
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
}
