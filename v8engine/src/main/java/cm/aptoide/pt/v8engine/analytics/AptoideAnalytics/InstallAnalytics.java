package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 19/06/17.
 */

public class InstallAnalytics extends AptoideAnalytics {

  private static final String APPLICATION_INSTALL = "Application Install";
  private static final String TYPE = "type";
  private static final String PACKAGE_NAME = "package_name";
  private static final String TRUSTED_BADGE = "trusted_badge";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public InstallAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void installed(String packageName) {
    analytics.sendEvent(
        new FacebookEvent(facebook, APPLICATION_INSTALL, createInstalledBundleData(packageName)));
  }

  private Bundle createInstalledBundleData(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, "Installed");
    bundle.putString(PACKAGE_NAME, packageName);
    return bundle;
  }

  public void replaced(String packageName, String trustedBadgge) {
    analytics.sendEvent(new FacebookEvent(facebook, APPLICATION_INSTALL,
        createReplacedBundleData(packageName, trustedBadgge)));
  }

  private Bundle createReplacedBundleData(String packageName, String trustedBadgge) {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, "Replaced");
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(TRUSTED_BADGE, trustedBadgge);
    return bundle;
  }
}
