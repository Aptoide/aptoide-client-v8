package cm.aptoide.pt.editorial;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.SplitAnalyticsMapper;
import cm.aptoide.pt.install.InstallAnalytics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscocalado on 03/09/2018.
 */

public class EditorialAnalytics {
  public static final String CURATION_CARD_INSTALL = "Curation_Card_Install";
  public static final String EDITORIAL_BN_CURATION_CARD_INSTALL =
      "Editorial_BN_Curation_Card_Install";
  public static final String REACTION_INTERACT = "Reaction_Interact";

  private static final String APPLICATION_NAME = "Application Name";
  private static final String TYPE = "type";
  private static final String WHERE = "where";
  private static final String ACTION = "action";
  private static final String CURATION_DETAIL = "curation_detail";
  private static final String CONTEXT = "context";
  private final DownloadAnalytics downloadAnalytics;
  private final InstallAnalytics installAnalytics;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final SplitAnalyticsMapper splitAnalyticsMapper;
  private final boolean fromHome;

  public EditorialAnalytics(DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, boolean fromHome, InstallAnalytics installAnalytics,
      SplitAnalyticsMapper splitAnalyticsMapper) {
    this.downloadAnalytics = downloadAnalytics;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.fromHome = fromHome;
    this.installAnalytics = installAnalytics;
    this.splitAnalyticsMapper = splitAnalyticsMapper;
  }

  public void setupDownloadEvents(RoomDownload download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action,
      String trustedBadge, String storeName, String installType, boolean isInCatappult) {
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        download.getVersionCode(), action, false, download.hasAppc(),
        download.hasSplits(), trustedBadge, null, storeName, installType, download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()), isInCatappult, "");

    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.EDITORIAL, action, false, false);
  }

  public void sendDownloadPauseEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "pause");
  }

  public void sendDownloadCancelEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "cancel");
  }

  public void clickOnInstallButton(String packageName, String type, boolean hasSplits,
      boolean hasBilling, boolean isMigration, String rank, String origin, String store,
      boolean hasObb, List<String> bdsFlags) {
    String context = getViewName(true);
    String installEvent = CURATION_CARD_INSTALL;
    if (!fromHome) {
      installEvent = EDITORIAL_BN_CURATION_CARD_INSTALL;
    }
    HashMap<String, Object> map = new HashMap<>();
    map.put(APPLICATION_NAME, packageName);
    map.put(TYPE, type);
    map.put(CONTEXT, context);

    installAnalytics.clickOnInstallEvent(packageName, type, hasSplits, hasBilling, isMigration,
        rank, origin, store, false, hasObb, bdsFlags.contains("STORE_BDS"), "");
    analyticsManager.logEvent(map, installEvent, AnalyticsManager.Action.CLICK, context);

    analyticsManager.logEvent(map, AppViewAnalytics.CLICK_INSTALL, AnalyticsManager.Action.CLICK,
        context);
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void sendReactionButtonClickEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "view_reactions");
    data.put(WHERE, CURATION_DETAIL);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendReactedEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "click_to_react");
    data.put(WHERE, CURATION_DETAIL);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendDeletedEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "delete_reaction");
    data.put(WHERE, CURATION_DETAIL);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendNotEnoughSpaceErrorEvent(String md5) {
    downloadAnalytics.sendNotEnoughSpaceError(md5);
  }
}
