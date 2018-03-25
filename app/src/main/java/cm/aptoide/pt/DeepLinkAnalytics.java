package cm.aptoide.pt;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose_messejana on 23-01-2018.
 */

public class DeepLinkAnalytics {
  public static final String FACEBOOK_APP_LAUNCH = "Aptoide Launch";
  public static final String APP_LAUNCH = "Application Launch";
  private static final String NEW_UPDATES_NOTIFICATION = "New Updates Available";
  private static final String DOWNLOADING_UPDATES = "Downloading Updates";
  private static final String TIMELINE_NOTIFICATION = "Timeline Notification";
  private static final String NEW_REPO = "New Repository";
  private static final String WEBSITE = "Website";
  private static final String URI = "Uri";
  private static final String SOURCE = "Source";
  private static final String LAUNCHER = "Launcher";
  private static final String SOURCE_GROUP_OPTION_APP_VIEW = "aptoide app view";
  private static final String SOURCE_GROUP_OPTION_HOME = "aptoide homepage";
  private static final String SOURCE_GROUP_OPTION_STORE = "aptoide store";
  private static final String SOURCE_GROUP_OPTION_BUNDLES = "aptoide bundle";
  private static final String SOURCE_GROUP_ATTRIBUTE = "source_group";
  private HashMap<String, Object> map = new HashMap<>();

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public DeepLinkAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void launcher() {
    analyticsManager.logEvent(createMap(SOURCE, LAUNCHER), FACEBOOK_APP_LAUNCH,
        AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void website(String uri) {
    map = new HashMap<>();
    map.put(SOURCE, WEBSITE);

    if (uri != null) {
      map.put(URI, uri.substring(0, uri.indexOf(":")));
    }
  }

  public void sendWebsite() {

    if (map != null && !map.isEmpty()) {
      analyticsManager.logEvent(map, FACEBOOK_APP_LAUNCH, AnalyticsManager.Action.AUTO,
          getViewName(true));
      analyticsManager.logEvent(map, APP_LAUNCH, AnalyticsManager.Action.AUTO, getViewName(true));
    }
    map = null;
  }

  private void websiteSourceGroup(String sourceGroupValue) {
    if (map != null && !map.isEmpty()) {
      map.put(SOURCE_GROUP_ATTRIBUTE, sourceGroupValue);
    }
  }

  public void websiteFromHomeWebPage() {
    websiteSourceGroup(SOURCE_GROUP_OPTION_HOME);
  }

  public void websiteFromAppViewWebPage() {
    websiteSourceGroup(SOURCE_GROUP_OPTION_APP_VIEW);
  }

  public void websiteFromBundlesWebPage() {
    websiteSourceGroup(SOURCE_GROUP_OPTION_BUNDLES);
  }

  public void websiteFromStoreWebPage() {
    websiteSourceGroup(SOURCE_GROUP_OPTION_STORE);
  }

  public void newUpdatesNotification() {
    analyticsManager.logEvent(createMap(SOURCE, NEW_UPDATES_NOTIFICATION), APP_LAUNCH,
        AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void downloadingUpdates() {
    analyticsManager.logEvent(createMap(SOURCE, DOWNLOADING_UPDATES), APP_LAUNCH,
        AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void timelineNotification() {
    analyticsManager.logEvent(createMap(SOURCE, TIMELINE_NOTIFICATION), APP_LAUNCH,
        AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void newRepo() {
    analyticsManager.logEvent(createMap(SOURCE, NEW_REPO), APP_LAUNCH, AnalyticsManager.Action.AUTO,
        getViewName(true));
  }

  private Map<String, Object> createMap(String key, String value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
