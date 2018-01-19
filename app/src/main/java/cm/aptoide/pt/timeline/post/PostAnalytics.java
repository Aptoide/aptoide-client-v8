
package cm.aptoide.pt.timeline.post;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 27/07/2017.
 */

public class PostAnalytics {
  public static final String OPEN_EVENT_NAME = "New_Post_Open";
  public static final String NEW_POST_EVENT_NAME = "New_Post_Close";
  public static final String POST_COMPLETE = "New_Post_Complete";
  private static final String RELATED_APPS_AVAILABLE = "related_apps_available";
  private static final String HAS_SELECTED_APP = "has_selected_app";
  private static final String PACKAGE_NAME = "package_name";
  private static final String HAS_COMMENT = "has_comment";
  private static final String HAS_URL = "has_url";
  public static final String POST = "POST";
  private static final String URL = "url";
  private static final String HAS_URL_PREVIEW = "has_url_preview";
  private static final String STATUS = "status";
  private static final String ERROR_MESSAGE = "error_message";
  private static final String ERROR_TYPE = "error_type";
  private static final String WEB_CODE = "web_code";
  private static final String EXTERNAL = "EXTERNAL";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public PostAnalytics(NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendOpenEvent(OpenSource source, boolean isExternal) {
    Map<String, Object> data = new HashMap<>();
    data.put("source", String.valueOf(source));
    analyticsManager.logEvent(data, OPEN_EVENT_NAME, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(false));
  }

  public void sendClosePostEvent(CloseType closeType, boolean isExternal) {
    Map<String, Object> data = new HashMap<>();
    data.put("closeType", String.valueOf(closeType));
    analyticsManager.logEvent(data, NEW_POST_EVENT_NAME, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompleteNoTextEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, hasSelectedApp, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "missing","No text inserted");
    Map<String, Object> aptoideData = createAptoideDataMap(false);

    analyticsManager.logEvent(facebookData, POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompleteNoSelectedAppEvent(boolean relatedAppsAvailable, boolean hasComment,
      boolean hasUrl, String url, boolean hasUrlPreview, boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, false, "",
        hasComment, hasUrl, url, hasUrlPreview, "missing","No app selected");
    Map<String, Object> aptoideData = createAptoideDataMap(false);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompleteNoLoginEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, hasSelectedApp, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "missing", "Not logged in");
    Map<String, Object> aptoideData = createAptoideDataMap(false);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompleteNoAppFoundEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, hasSelectedApp, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "missing", "App not found");
    Map<String, Object> aptoideData = createAptoideDataMap(false);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompletedNetworkFailedEvent(boolean relatedAppsAvailable,
      boolean hasSelectedApp, String packageName, boolean hasComment, boolean hasUrl, String url,
      boolean hasUrlPreview, String errorCode, boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, hasSelectedApp, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "fail", "Network error");
    Map<String, Object> aptoideData = createAptoideDataMap(false);
    facebookData.put(WEB_CODE,errorCode);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostCompleteEvent(boolean relatedAppsAvailable, String packageName,
      boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview, boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, true, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "success", "");
    Map<String, Object> aptoideData = createAptoideDataMap(true);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
    analyticsManager.logEvent(aptoideData, POST, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  public void sendPostFailedEvent(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview,
      String errorType, boolean isExternal) {

    Map<String, Object> facebookData = decorateMap(relatedAppsAvailable, hasSelectedApp, packageName,
        hasComment, hasUrl, url, hasUrlPreview, "fail", "");
    facebookData.put(ERROR_TYPE,errorType);

    analyticsManager.logEvent(facebookData,POST_COMPLETE, AnalyticsManager.Action.CLICK, isExternal ? EXTERNAL : getViewName(true));
  }

  private Map<String, Object> decorateMap(boolean relatedAppsAvailable, boolean hasSelectedApp,
      String packageName, boolean hasComment, boolean hasUrl, String url, boolean hasUrlPreview, String status, String errorMessage) {
    Map<String, Object> data = new HashMap<>();
    data.put(RELATED_APPS_AVAILABLE, String.valueOf(relatedAppsAvailable));
    data.put(HAS_SELECTED_APP, String.valueOf(hasSelectedApp));
    data.put(HAS_COMMENT, String.valueOf(hasComment));
    data.put(HAS_URL, String.valueOf(hasUrl));
    data.put(HAS_URL_PREVIEW, String.valueOf(hasUrlPreview));
    data.put(PACKAGE_NAME, packageName);
    data.put(URL, url);
    data.put(STATUS, status);
    data.put(ERROR_MESSAGE, errorMessage);
    return data;
  }

  private String getViewName(boolean isCurrent){
    String viewName = "";
    if(isCurrent){
      viewName = navigationTracker.getCurrentViewName();
    }
    else{
      viewName = navigationTracker.getPreviousViewName();
    }
    if(viewName.equals("")) {
      return "PostAnalytics"; //Default value, shouldn't get here
    }
    return viewName;
  }

  @NonNull
  private HashMap<String, Object> createAptoideDataMap(boolean success) {
    HashMap<String, Object> data = new HashMap<>();
    data.put("status", success ? "success" : "fail");
    data.put("previous_context", navigationTracker.getPreviousScreen()
        .getFragment());
    data.put("store", navigationTracker.getPreviousScreen()
        .getStore());
    return data;
  }

  enum OpenSource {
    APP_TIMELINE, EXTERNAL
  }

  enum CloseType {
    X, BACK
  }
}
