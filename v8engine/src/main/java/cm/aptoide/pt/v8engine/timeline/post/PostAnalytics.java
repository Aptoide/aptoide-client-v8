package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by trinkes on 27/07/2017.
 */

public class PostAnalytics {
  private static final String OPEN_EVENT_NAME = "New_Post_Open";
  private static final String NEW_POST_EVENT_NAME = "New_Post_Close";
  private static final String POST_COMPLETE = "New_Post_Complete";
  private static final String RELATED_APPS_AVAILABLE = "related_apps_available";
  private static final String HAS_SELECTED_APP = "has_selected_app";
  private static final String PACKAGE_NAME = "package_name";
  private static final String HAS_COMMENT = "has_comment";
  private static final String HAS_URL = "has_url";
  private static final String URL = "url";
  private static final String HAS_URL_PREVIEW = "has_url_preview";
  private static final String STATUS = "status";
  private static final String ERROR_MESSAGE = "error_message";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public PostAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendOpenEvent(OpenSource source) {
    Bundle bundle = new Bundle();
    bundle.putString("source", String.valueOf(source));
    analytics.sendEvent(new FacebookEvent(facebook, OPEN_EVENT_NAME, bundle));
  }

  public void sendClosePostEvent(CloseType closeType) {
    Bundle bundle = new Bundle();
    bundle.putString("method", String.valueOf(closeType));
    analytics.sendEvent(new FacebookEvent(facebook, NEW_POST_EVENT_NAME, bundle));
  }

  public void sendPostCompleteNoTextEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompleteNoTextEventBundle(relatedAppsAvailable, hasSelectedApp, packageName,
            hasComment, hasUrl, url, hasUrlPreview)));
  }

  private Bundle createPostCompleteNoTextEventBundle(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, "fail");
    bundle.putString(ERROR_MESSAGE, "No text inserted");
    return bundle;
  }

  public void sendPostCompleteNoSelectedAppEvent(boolean relatedAppsAvailable, boolean hasComment,
      boolean hasUrl, String url, boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompleteNoSelectedAppEventBundle(relatedAppsAvailable, hasComment, hasUrl, url,
            hasUrlPreview)));
  }

  private Bundle createPostCompleteNoSelectedAppEventBundle(boolean relatedAppsAvailable,
      boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(false));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, "");
    bundle.putString(URL, url);
    bundle.putString(STATUS, "fail");
    bundle.putString(ERROR_MESSAGE, "No app selected");
    return bundle;
  }

  public void sendPostCompleteNoLoginEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNoLoginEventBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment,
            hasUrl, url, hasUrlPreview)));
  }

  private Bundle createNoLoginEventBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, "fail");
    bundle.putString(ERROR_MESSAGE, "Not logged in");
    return bundle;
  }

  public void sendPostCompleteNoAppFoundEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNoAppFoundEventBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment,
            hasUrl, url, hasUrlPreview)));
  }

  private Bundle createNoAppFoundEventBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, "fail");
    bundle.putString(ERROR_MESSAGE, "App not found");
    return bundle;
  }

  public void sendPostCompletedNetworkFailedEvent(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNetworkFailedEventBundle(relatedAppsAvailable, hasSelectedApp, packageName,
            hasComment, hasUrl, url, hasUrlPreview)));
  }

  private Bundle createNetworkFailedEventBundle(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, "fail");
    bundle.putString(ERROR_MESSAGE, "Network error");
    return bundle;
  }

  public void sendPostCompleteEvent(boolean relatedAppsAvailable, String packageName,
      boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompletedSuccessEventBundle(relatedAppsAvailable, packageName, hasComment, hasUrl,
            url, hasUrlPreview)));
  }

  private Bundle createPostCompletedSuccessEventBundle(boolean relatedAppsAvailable,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(true));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, "success");
    bundle.putString(ERROR_MESSAGE, "");
    return bundle;
  }

  enum OpenSource {
    APP_TIMELINE, EXTERNAL
  }

  enum CloseType {
    X, BACK
  }
}
