package cm.aptoide.pt.app;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 10/05/17.
 */

public class AppViewAnalytics {

  private static final String ACTION = "Action";
  private static final String APP_VIEW_INTERACT = "App_View_Interact";
  private Analytics analytics;
  private AppEventsLogger facebook;

  public AppViewAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
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
