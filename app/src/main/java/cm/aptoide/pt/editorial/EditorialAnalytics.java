package cm.aptoide.pt.editorial;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;
import java.util.HashMap;

/**
 * Created by franciscocalado on 03/09/2018.
 */

public class EditorialAnalytics {
  public static final String CURATION_CARD_INSTALL = "Curation_Card_Install";
  public static final String EDITORIAL_BN_CURATION_CARD_INSTALL =
      "Editorial_BN_Curation_Card_Install";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String TYPE = "type";

  private final DownloadAnalytics downloadAnalytics;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final boolean fromHome;

  public EditorialAnalytics(DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, boolean fromHome) {
    this.downloadAnalytics = downloadAnalytics;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.fromHome = fromHome;
  }

  public void setupDownloadEvents(Download download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.EDITORIAL, action);
  }

  public void sendDownloadPauseEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "pause");
  }

  public void sendDownloadCancelEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "cancel");
  }

  public void clickOnInstallButton(String packageName, String type) {
    String installEvent = CURATION_CARD_INSTALL;
    if (!fromHome) {
      installEvent = EDITORIAL_BN_CURATION_CARD_INSTALL;
    }
    HashMap<String, Object> map = new HashMap<>();
    map.put(APPLICATION_NAME, packageName);
    map.put(TYPE, type);
    analyticsManager.logEvent(map, installEvent, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void sendReactionButtonClickEvent(String cardId) {
    //TODO
  }
}
