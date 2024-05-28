package cm.aptoide.pt.promotions;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.InstallAnalytics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionsAnalytics {
  public static final String PROMOTION_DIALOG = "Promotion_Dialog";
  public static final String PROMOTIONS_INTERACT = "Promotions_Interact";
  public static final String VALENTINE_MIGRATOR = "Valentine_Migrator";
  private static final String ACTION = "action";
  private static final String ACTION_CLAIM = "claim";
  private static final String ACTION_UPDATE = "update";
  private static final String ACTION_INSTALL = "install";
  private static final String AMOUNT = "amount";
  private static final String PACKAGE = "package";
  private static final String VIEW = "view";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String CONTEXT = "context";
  private final String NEXT = "next";
  private final String CANCEL = "cancel";
  private final String OPEN_WALLET = "open wallet";
  private final String WALLET_DIALOG = "wallet dialog";
  private final String SIGNATURE = "signature";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final DownloadAnalytics downloadAnalytics;
  private final InstallAnalytics installAnalytics;
  private final String ACTION_OPEN = "open";

  public PromotionsAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
  }

  public void setupDownloadEvents(RoomDownload download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action,
      Origin origin, boolean isAppBundle) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.PROMOTIONS, action, false, origin, false);
    downloadAnalytics.downloadCompleteEvent(download.getMd5(), download.getPackageName(), "",
        action, isAppBundle);
  }

  public void sendOpenPromotionsFragmentEvent() {
    analyticsManager.logEvent(putPromotionAppAction(ACTION_OPEN, new HashMap<>()),
        PROMOTIONS_INTERACT, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  private HashMap<String, Object> putPromotionAppAction(String action,
      HashMap<String, Object> map) {
    map.put(ACTION, action);
    return map;
  }

  private HashMap<String, Object> createPromotionsInteractMap(String action, String packageName,
      float appcValue) {
    HashMap<String, Object> map = new HashMap<>();
    map = putPromotionAppAction(action, map);
    map.put(PACKAGE, packageName);
    map.put(AMOUNT, appcValue);
    return map;
  }

  public void sendClickOnWalletDialogNext(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, NEXT);
    data.put(PACKAGE, packageName);
    data.put(VIEW, WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnWalletDialogCancel(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, CANCEL);
    data.put(PACKAGE, packageName);
    data.put(VIEW, WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnWalletDialogFindWallet(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, OPEN_WALLET);
    data.put(PACKAGE, packageName);
    data.put(VIEW, WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsAppInteractClaimEvent(String packageName, float appcValue) {
    analyticsManager.logEvent(createPromotionsInteractMap(ACTION_CLAIM, packageName, appcValue),
        PROMOTIONS_INTERACT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPromotionsAppInteractInstallEvent(String packageName, float appcValue,
      DownloadModel.Action action, boolean hasSplits, boolean hasBilling, String rank,
      String origin, String store, boolean hasObb, List<String> bdsFlags) {
    String context = getViewName(true);
    String downloadAction = null;

    if (action == DownloadModel.Action.INSTALL) {
      downloadAction = ACTION_INSTALL;
    } else if (action == DownloadModel.Action.UPDATE) {
      downloadAction = ACTION_UPDATE;
    }

    HashMap<String, Object> map = new HashMap<>();
    map.put(APPLICATION_NAME, packageName);
    map.put(CONTEXT, context);
    map.put(ACTION, downloadAction);

    if (downloadAction != null) {
      installAnalytics.clickOnInstallEvent(packageName, downloadAction, hasSplits, hasBilling,
          downloadAction.equals(DownloadModel.Action.MIGRATE.toString()), rank, origin,
          store, false, hasObb, bdsFlags.contains("STORE_BDS"), "");
      analyticsManager.logEvent(createPromotionsInteractMap(downloadAction, packageName, appcValue),
          PROMOTIONS_INTERACT, AnalyticsManager.Action.CLICK, context);
      analyticsManager.logEvent(map, AppViewAnalytics.CLICK_INSTALL, AnalyticsManager.Action.CLICK,
          context);
    }
  }

  public void sendValentineMigratorEvent(String packageName, Boolean signatureMatch) {
    final Map<String, Object> data = new HashMap<>();
    data.put(PACKAGE, packageName);
    data.put(SIGNATURE, signatureMatch ? "same" : "different");

    analyticsManager.logEvent(data, VALENTINE_MIGRATOR, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendNotEnoughSpaceErrorEvent(String md5) {
    downloadAnalytics.sendNotEnoughSpaceError(md5);
  }
}
