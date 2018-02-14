package cm.aptoide.pt.app;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadAnalytics;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 10/05/17.
 */

public class AppViewAnalytics {

  public static final String EDITORS_CHOICE_CLICKS = "Editors_Choice_Clicks";
  public static final String HOME_PAGE_EDITORS_CHOICE_FLURRY = "Home_Page_Editors_Choice";
  public static final String APP_VIEW_OPEN_FROM = "App_Viewed_Open_From";
  public static final String OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String APP_VIEW_INTERACT = "App_View_Interact";
  public static final String DOWNGRADE_DIALOG = "Downgrade_Dialog";
  public static final String CLICK_INSTALL = "Clicked on install button";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String APPLICATION_PUBLISHER = "Application Publisher";
  private static final String ACTION = "Action";
  private static final String APP_SHORTCUT = "App_Shortcut";
  private final DownloadAnalytics downloadAnalytics;
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public AppViewAnalytics(DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.downloadAnalytics = downloadAnalytics;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendEditorsChoiceClickEvent(ScreenTagHistory previousScreen, String packageName,
      String editorsBrickPosition) {
    analyticsManager.logEvent(
        createEditorsChoiceClickEventMap(previousScreen, packageName, editorsBrickPosition),
        EDITORS_CHOICE_CLICKS, AnalyticsManager.Action.CLICK, getViewName(false));
    analyticsManager.logEvent(
        createEditorsClickEventMap(previousScreen, packageName, editorsBrickPosition),
        HOME_PAGE_EDITORS_CHOICE_FLURRY, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  private Map<String, Object> createEditorsClickEventMap(ScreenTagHistory previousScreen,
      String packageName, String editorsBrickPosition) {
    Map<String, Object> map = new HashMap<>();
    map.put("Application Name", packageName);
    map.put("Search Position", editorsBrickPosition);
    if (previousScreen != null && previousScreen.getFragment() != null) {
      map.put("fragment", previousScreen.getFragment());
    }
    return map;
  }

  private Map<String, Object> createEditorsChoiceClickEventMap(ScreenTagHistory previousScreen,
      String packageName, String editorsBrickPosition) {
    Map<String, Object> map = new HashMap<>();
    if (previousScreen != null && previousScreen.getFragment() != null) {
      map.put("fragment", previousScreen.getFragment());
    }
    map.put("package_name", packageName);
    map.put("position", editorsBrickPosition);
    return map;
  }

  public void sendAppViewOpenedFromEvent(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge) {
    analyticsManager.logEvent(
        createAppViewedFromMap(previousScreen, currentScreen, packageName, appPublisher, badge),
        APP_VIEW_OPEN_FROM, AnalyticsManager.Action.CLICK, getViewName(false));
    analyticsManager.logEvent(createAppViewDataMap(previousScreen, currentScreen, packageName),
        OPEN_APP_VIEW, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  private Map<String, Object> createAppViewDataMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName) {
    Map<String, String> packageMap = new HashMap<>();
    packageMap.put("package", packageName);
    Map<String, Object> data = new HashMap<>();
    data.put("app", packageMap);
    if (previousScreen != null) {
      data.put("previous_store", previousScreen.getStore());
    } else {
      data.put("previous_store", APP_SHORTCUT);
    }
    if (currentScreen != null) {
      data.put("previous_tag", currentScreen.getTag());
    } else {
      data.put("previous_tag", APP_SHORTCUT);
    }
    return data;
  }

  private HashMap<String, Object> createAppViewedFromMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge)
      throws NullPointerException {
    HashMap<String, Object> map = new HashMap<>();
    if (previousScreen != null) {
      if (previousScreen.getFragment() != null) {
        map.put("fragment", previousScreen.getFragment());
      }
      if (previousScreen.getStore() != null) {
        map.put("store", previousScreen.getStore());
      }
    }
    if (currentScreen != null) {
      if (currentScreen.getTag() != null) {
        map.put("tag", currentScreen.getTag());
      }
    }
    map.put("package_name", packageName);
    map.put("application_publisher", appPublisher);
    map.put("trusted_badge", badge);
    return map;
  }

  public void sendOpenScreenshotEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Open Screenshot"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenVideoEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Open Video"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendRateThisAppEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Rate This App"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendReadAllEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Read All"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.VIEW, getViewName(true));
  }

  public void sendFollowStoreEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Follow Store"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenStoreEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Open Store"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOtherVersionsEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Other Versions"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendReadMoreEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Read More"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendFlagAppEvent(String flagDetail) {
    analyticsManager.logEvent(createFlagAppEventData("Flag App", flagDetail), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createFlagAppEventData(String action, String flagDetail) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    map.put("flag_details", flagDetail);
    return map;
  }

  public void sendBadgeClickEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Open Badge"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAppShareEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "App Share"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendScheduleDownloadEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Schedule Download"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendRemoteInstallEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Install on TV"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.INSTALL, getViewName(true));
  }

  public void downgradeDialogContinue() {
    analyticsManager.logEvent(createMapData(ACTION, "Continue"), DOWNGRADE_DIALOG,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void downgradeDialogCancel() {
    analyticsManager.logEvent(createMapData(ACTION, "Cancel"), DOWNGRADE_DIALOG,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void clickOnInstallButton(GetAppMeta.App app) {
    try {
      HashMap<String, Object> map = new HashMap<>();

      map.put(APPLICATION_NAME, app.getPackageName());
      map.put(APPLICATION_PUBLISHER, app.getDeveloper()
          .getName());

      analyticsManager.logEvent(map, CLICK_INSTALL, AnalyticsManager.Action.CLICK,
          getViewName(true));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void setupDownloadEvents(Download download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.APPVIEW, action);
  }
}
