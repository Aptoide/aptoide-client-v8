package cm.aptoide.pt.timeline.post;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.events.AptoideEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

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
  private static final String POST = "POST";
  private static final String URL = "url";
  private static final String HAS_URL_PREVIEW = "has_url_preview";
  private static final String STATUS = "status";
  private static final String ERROR_MESSAGE = "error_message";
  private static final String ERROR_TYPE = "error_type";
  private static final String WEB_CODE = "web_code";
  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String appId;
  private final SharedPreferences sharedPreferences;
  private final NavigationTracker navigationTracker;

  public PostAnalytics(Analytics analytics, AppEventsLogger facebook,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, String appId,
      SharedPreferences sharedPreferences, NavigationTracker navigationTracker) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
    this.navigationTracker = navigationTracker;
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
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompleteNoTextEventBundle(relatedAppsAvailable, hasSelectedApp, packageName,
            hasComment, hasUrl, url, hasUrlPreview)));
    analytics.sendEvent(createAptoideEvent(POST, false, isExternal));
  }

  @NonNull
  private AptoideEvent createAptoideEvent(String eventName, boolean success, boolean isExternal) {
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> result = new HashMap<>();
    result.put("status", success ? "success" : "fail");
    data.put("previous_context", navigationTracker.getPreviousScreen()
        .getFragment());
    data.put("store", navigationTracker.getPreviousScreen()
        .getStore());
    data.put("result", result);
    return new AptoideEvent(data, eventName, "CLICK", isExternal ? "EXTERNAL" : "TIMELINE",
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, appId, sharedPreferences);
  }

  @NonNull private AptoideEvent createAptoideCompletedEvent(String eventName, String packageName,
      boolean success, boolean isExternal) {
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> specific = new HashMap<>();
    specific.put("app", packageName);
    data.put("specific", specific);
    data.put("status", success ? "success" : "fail");
    data.put("previous_context", navigationTracker.getPreviousScreen()
        .getFragment());
    data.put("store", navigationTracker.getPreviousScreen()
        .getStore());
    return new AptoideEvent(data, eventName, "CLICK", isExternal ? "EXTERNAL" : "TIMELINE",
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, appId, sharedPreferences);
  }

  private Bundle createPostCompleteNoTextEventBundle(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment, hasUrl, url,
        hasUrlPreview, bundle, "missing", "No text inserted");
    return bundle;
  }

  public void sendPostCompleteNoSelectedAppEvent(boolean relatedAppsAvailable, boolean hasComment,
      boolean hasUrl, String url, boolean hasUrlPreview, boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompleteNoSelectedAppEventBundle(relatedAppsAvailable, hasComment, hasUrl, url,
            hasUrlPreview)));
    analytics.sendEvent(createAptoideEvent(POST, false, isExternal));
  }

  private Bundle createPostCompleteNoSelectedAppEventBundle(boolean relatedAppsAvailable,
      boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, false, "", hasComment, hasUrl, url, hasUrlPreview, bundle,
        "missing", "No app selected");
    return bundle;
  }

  public void sendPostCompleteNoLoginEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNoLoginEventBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment,
            hasUrl, url, hasUrlPreview)));
    analytics.sendEvent(createAptoideEvent(POST, false, isExternal));
  }

  private Bundle createNoLoginEventBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment, hasUrl, url,
        hasUrlPreview, bundle, "missing", "Not logged in");
    return bundle;
  }

  public void sendPostCompleteNoAppFoundEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNoAppFoundEventBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment,
            hasUrl, url, hasUrlPreview)));
    analytics.sendEvent(createAptoideEvent(POST, false, isExternal));
  }

  private Bundle createNoAppFoundEventBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment, hasUrl, url,
        hasUrlPreview, bundle, "missing", "App not found");
    return bundle;
  }

  private void decorateBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      Bundle bundle, String status, String errorMessage) {
    bundle.putString(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(HAS_COMMENT, String.valueOf(hasComment));
    bundle.putString(HAS_URL, String.valueOf(hasUrl));
    bundle.putString(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    bundle.putString(PACKAGE_NAME, packageName);
    bundle.putString(URL, url);
    bundle.putString(STATUS, status);
    bundle.putString(ERROR_MESSAGE, errorMessage);
  }

  public void sendPostCompletedNetworkFailedEvent(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview, String errorCode, boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createNetworkFailedEventBundle(relatedAppsAvailable, hasSelectedApp, packageName,
            hasComment, hasUrl, url, hasUrlPreview, errorCode)));
    analytics.sendEvent(createAptoideEvent(POST, false, isExternal));
  }

  private Bundle createNetworkFailedEventBundle(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview, String errorCode) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment, hasUrl, url,
        hasUrlPreview, bundle, "fail", "Network error");
    bundle.putString(WEB_CODE, errorCode);
    return bundle;
  }

  public void sendPostCompleteEvent(boolean relatedAppsAvailable, String packageName,
      boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview, boolean isExternal) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostCompletedSuccessEventBundle(relatedAppsAvailable, packageName, hasComment, hasUrl,
            url, hasUrlPreview)));
    analytics.sendEvent(createAptoideEvent(POST, true, isExternal));
  }

  private Bundle createPostCompletedSuccessEventBundle(boolean relatedAppsAvailable,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview) {
    Bundle bundle = new Bundle();
    decorateBundle(relatedAppsAvailable, true, packageName, hasComment, hasUrl, url, hasUrlPreview,
        bundle, "success", "");
    return bundle;
  }

  public void sendPostFailedEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      String errorType) {
    analytics.sendEvent(new FacebookEvent(facebook, POST_COMPLETE,
        createPostFailEventBundle(relatedAppsAvailable, hasSelectedApp, packageName, hasComment,
            hasUrl, url, hasUrlPreview, errorType)));
  }

  private Bundle createPostFailEventBundle(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      String errorType) {
    Bundle bundle = new Bundle();
    bundle.putString(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    bundle.putString(ERROR_TYPE, errorType);
    decorateBundle(relatedAppsAvailable, true, packageName, hasComment, hasUrl, url, hasUrlPreview,
        bundle, "fail", "");
    return bundle;
  }

  enum OpenSource {
    APP_TIMELINE, EXTERNAL
  }

  enum CloseType {
    X, BACK
  }
}
