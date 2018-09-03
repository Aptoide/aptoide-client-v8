package cm.aptoide.pt.app.view;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;
import java.util.HashMap;

/**
 * Created by franciscocalado on 03/09/2018.
 */

public class EditorialAnalytics {
  private static final String CLICK_INSTALL = "Clicked on install button";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String APPLICATION_PUBLISHER = "Application Publisher";
  private static final String ACTION = "Action";
  private static final String APP_SHORTCUT = "App_Shortcut";
  private static final String TYPE = "type";

  private final DownloadAnalytics downloadAnalytics;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public EditorialAnalytics(DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.downloadAnalytics = downloadAnalytics;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void setupDownloadEvents(Download download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.APPVIEW, action);
  }

  public void sendDownloadPauseEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "pause");
  }

  public void sendDownloadCancelEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "cancel");
  }

  public void clickOnInstallButton(String packageName, String type) {
    HashMap<String, Object> map = new HashMap<>();
    map.put(TYPE, type);
    map.put(APPLICATION_NAME, packageName);
    analyticsManager.logEvent(map, CLICK_INSTALL, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
