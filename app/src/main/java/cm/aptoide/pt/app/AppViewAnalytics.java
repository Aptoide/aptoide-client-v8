package cm.aptoide.pt.app;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 10/05/17.
 */

public class AppViewAnalytics {

  public static final String EDITORS_CHOICE_CLICKS = "Editors_Choice_Clicks";
  public static final String HOME_PAGE_EDITORS_CHOICE_FLURRY = "Home_Page_Editors_Choice";
  private static final String ACTION = "Action";
  private static final String APP_VIEW_INTERACT = "App_View_Interact";
  private Analytics analytics;
  private AppEventsLogger facebook;

  public AppViewAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendEditorsChoiceClickEvent(ScreenTagHistory previousScreen, String packageName,
      String editorsBrickPosition) {
    analytics.sendEvent(new FacebookEvent(facebook, EDITORS_CHOICE_CLICKS,
        createEditorsChoiceClickEventBundle(previousScreen, packageName, editorsBrickPosition)));
    analytics.sendEvent(new FlurryEvent(HOME_PAGE_EDITORS_CHOICE_FLURRY,
        createEditorsClickEventMap(previousScreen, packageName, editorsBrickPosition)));
  }

  private Map<String, String> createEditorsClickEventMap(ScreenTagHistory previousScreen,
      String packageName, String editorsBrickPosition) {
    Map<String, String> map = new HashMap<>();
    map.put("Application Name", packageName);
    map.put("Search Position", editorsBrickPosition);
    if (previousScreen != null && previousScreen.getFragment() != null) {
      map.put("fragment", previousScreen.getFragment());
    }
    return map;
  }

  private Bundle createEditorsChoiceClickEventBundle(ScreenTagHistory previousScreen,
      String packageName, String editorsBrickPosition) {
    Bundle bundle = new Bundle();
    if (previousScreen.getFragment() != null) {
      bundle.putString("fragment", previousScreen.getFragment());
    }
    bundle.putString("package_name", packageName);
    bundle.putString("position", editorsBrickPosition);
    return bundle;
  }

  public void sendAppViewOpenedFromEvent(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge) {
    analytics.sendEvent(new FacebookEvent(facebook, "App_Viewed_Open_From",
        createAppViewedFromBundle(previousScreen, currentScreen, packageName, appPublisher,
            badge)));
    analytics.sendEvent(new FlurryEvent("App_Viewed_Open_From",
        createAppViewedFromMap(previousScreen, currentScreen, packageName, appPublisher, badge)));
  }

  private Map<String, String> createAppViewedFromMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge) {
    Map<String, String> map = new HashMap<>();
    if (previousScreen.getFragment() != null) {
      map.put("fragment", previousScreen.getFragment());
    }
    if (currentScreen.getTag() != null) {
      map.put("tag", currentScreen.getTag());
    }
    if (previousScreen.getStore() != null) {
      map.put("store", previousScreen.getStore());
    }
    map.put("package_name", packageName);
    map.put("application_publisher", appPublisher);
    map.put("trusted_badge", badge);
    return map;
  }

  private Bundle createAppViewedFromBundle(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge)
      throws NullPointerException {
    Bundle bundle = new Bundle();
    if (previousScreen.getFragment() != null) {
      bundle.putString("fragment", previousScreen.getFragment());
    }
    if (currentScreen.getTag() != null) {
      bundle.putString("tag", currentScreen.getTag());
    }
    if (previousScreen.getStore() != null) {
      bundle.putString("store", previousScreen.getStore());
    }
    bundle.putString("package_name", packageName);
    bundle.putString("application_publisher", appPublisher);
    bundle.putString("trusted_badge", badge);
    return bundle;
  }

  public void sendOpenScreenshotEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, APP_VIEW_INTERACT,
        createBundleData(ACTION, "Open Screenshot")));
  }

  public void sendOpenVideoEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Open Video")));
  }

  public void sendRateThisAppEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Rate This App")));
  }

  public void sendReadAllEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Read All")));
  }

  public void sendFollowStoreEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Follow Store")));
  }

  public void sendOpenStoreEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Open Store")));
  }

  public void sendOtherVersionsEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Other Versions")));
  }

  public void sendReadMoreEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Read More")));
  }

  public void sendOpenRecommendedAppEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, APP_VIEW_INTERACT,
        createBundleData(ACTION, "Open Recommended App")));
  }

  public void sendFlagAppEvent(String flagDetail) {
    analytics.sendEvent(new FacebookEvent(facebook, APP_VIEW_INTERACT,
        createFlagAppEventData("Flag App", flagDetail)));
  }

  private Bundle createFlagAppEventData(String action, String flagDetail) {
    Bundle bundle = new Bundle();
    bundle.putString(ACTION, action);
    bundle.putString("flag_details", flagDetail);
    return bundle;
  }

  public void sendBadgeClickEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Open Badge")));
  }

  public void sendAppShareEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "App Share")));
  }

  public void sendScheduleDownloadEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, APP_VIEW_INTERACT,
        createBundleData(ACTION, "Schedule Download")));
  }

  public void sendRemoteInstallEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, APP_VIEW_INTERACT, createBundleData(ACTION, "Install on TV")));
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }
}
