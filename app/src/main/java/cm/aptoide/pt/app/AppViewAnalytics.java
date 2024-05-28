package cm.aptoide.pt.app;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreAnalytics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Created by pedroribeiro on 10/05/17.
 */

public class AppViewAnalytics {

  public static final String EDITORS_CHOICE_CLICKS = "Editors_Choice_Clicks";
  public static final String HOME_PAGE_EDITORS_CHOICE_FLURRY = "Home_Page_Editors_Choice";
  public static final String APP_VIEW_OPEN_FROM = "App_Viewed_Open_From";
  public static final String OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String APP_VIEW_INTERACT = "App_View_Interact";
  public static final String CLICK_INSTALL = "Clicked on install button";
  public static final String SIMILAR_APP_INTERACT = "Similar_App_Interact";
  public static final String ADS_BLOCK_BY_OFFER = "Ads_Block_By_Offer";
  public static final String APPC_SIMILAR_APP_INTERACT = "Appc_Similar_App_Interact";
  public static final String BONUS_MIGRATION_APPVIEW = "Bonus_Migration_19_App_View";
  public static final String BONUS_GAME_WALLET_OFFER_19 = "Bonus_Game_Wallet_Offer_19_App_View";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String APPLICATION_PUBLISHER = "Application Publisher";
  private static final String ACTION = "Action";
  private static final String APP_SHORTCUT = "App_Shortcut";
  private static final String CONTEXT = "context";
  private static final String TYPE = "type";
  private static final String NETWORK = "network";
  private static final String IS_AD = "Is_ad";
  private static final String POSITION = "Position";
  private static final String PACKAGE_NAME = "Package_name";
  private static final String IMPRESSION = "impression";
  private static final String TAP_ON_APP = "tap_on_app";
  private static final String APP_BUNDLE = "app_bundle";
  private static final String IS_APKFY = "apkfy_app_install";

  private final DownloadAnalytics downloadAnalytics;
  private final InstallAnalytics installAnalytics;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final StoreAnalytics storeAnalytics;

  public AppViewAnalytics(DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, StoreAnalytics storeAnalytics,
      InstallAnalytics installAnalytics) {
    this.downloadAnalytics = downloadAnalytics;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.storeAnalytics = storeAnalytics;
    this.installAnalytics = installAnalytics;
  }

  public void sendEditorsChoiceClickEvent(String packageName, String editorsBrickPosition) {
    analyticsManager.logEvent(
        createEditorsChoiceClickEventMap(navigationTracker.getPreviousScreen(), packageName,
            editorsBrickPosition), EDITORS_CHOICE_CLICKS, AnalyticsManager.Action.CLICK,
        getViewName(false));
    analyticsManager.logEvent(
        createEditorsClickEventMap(navigationTracker.getPreviousScreen(), packageName,
            editorsBrickPosition), HOME_PAGE_EDITORS_CHOICE_FLURRY, AnalyticsManager.Action.CLICK,
        getViewName(false));
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

  public void sendAppViewOpenedFromEvent(String packageName, String appPublisher, String badge,
      boolean hasBilling, boolean hasAdvertising) {
    analyticsManager.logEvent(createAppViewedFromMap(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), packageName, appPublisher, badge, hasBilling,
        hasAdvertising), APP_VIEW_OPEN_FROM, AnalyticsManager.Action.CLICK, getViewName(false));
    analyticsManager.logEvent(createAppViewDataMap(navigationTracker.getPreviousScreen(),
            navigationTracker.getCurrentScreen(), packageName, hasBilling, hasAdvertising),
        OPEN_APP_VIEW, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  private Map<String, Object> createAppViewDataMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, boolean hasBilling,
      boolean hasAdvertising) {
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

    data.put("appcoins_type", mapAppCoinsInfo(hasBilling, hasAdvertising));

    return data;
  }

  private String mapAppCoinsInfo(boolean hasBilling, boolean hasAdvertising) {
    if (hasBilling && hasAdvertising) {
      return "AppCoins Ads IAB";
    } else if (hasBilling) {
      return "AppCoins IAB";
    } else if (hasAdvertising) {
      return "AppCoins Ads";
    }
    return "None";
  }

  private HashMap<String, Object> createAppViewedFromMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String appPublisher, String badge,
      boolean hasBilling, boolean hasAdvertising) throws NullPointerException {
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

    map.put("appcoins_type", mapAppCoinsInfo(hasBilling, hasAdvertising));

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

  public void sendSimilarAppsInteractEvent(String type) {
    analyticsManager.logEvent(createSimilarAppsEventData(type), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAppcInfoInteractEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "AppCoins Info View"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createFlagAppEventData(String action, String flagDetail) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    map.put("flag_details", flagDetail);
    return map;
  }

  private Map<String, Object> createSimilarAppsEventData(String type) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, "Open App on Recommended for you");
    map.put("bundle_tag", type);
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

  public void sendRemoteInstallEvent() {
    analyticsManager.logEvent(createMapData(ACTION, "Install on TV"), APP_VIEW_INTERACT,
        AnalyticsManager.Action.INSTALL, getViewName(true));
  }

  public void clickOnInstallButton(String packageName, String developerName, String type,
      boolean hasSplits, boolean hasBilling, boolean isMigration, String rank,
      String origin, String store, boolean isApkfy, boolean hasObb, List<String> bdsFlags,
      String appCategory) {
    String context = getViewName(true);
    HashMap<String, Object> map = new HashMap<>();
    map.put(TYPE, type);
    map.put(APPLICATION_NAME, packageName);
    map.put(APPLICATION_PUBLISHER, developerName);
    map.put(APP_BUNDLE, hasSplits);
    map.put(CONTEXT, context);
    map.put(IS_APKFY, isApkfy);

    installAnalytics.clickOnInstallEvent(packageName, type, hasSplits, hasBilling, isMigration,
        rank, origin, store, isApkfy, hasObb, bdsFlags.contains("STORE_BDS"), appCategory);
    analyticsManager.logEvent(map, CLICK_INSTALL, AnalyticsManager.Action.CLICK, context);
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void setupDownloadEvents(RoomDownload download, int campaignId, String abTestGroup,
      DownloadModel.Action downloadAction, AnalyticsManager.Action action, String trustedValue,
      String editorsChoice, String storeName, boolean isApkfy, String splitTypes,
      boolean isInCatappult, String appCategory) {
    if (DownloadModel.Action.MIGRATE.equals(downloadAction)) {
      downloadAnalytics.migrationClicked(download.getMd5(), download.getVersionCode(),
          download.getPackageName(), trustedValue, editorsChoice, InstallType.UPDATE_TO_APPC,
          action, download.hasAppc(), download.hasSplits(), storeName, isApkfy,
          download.hasObbs(), splitTypes, isInCatappult, appCategory);
      downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
          DownloadAnalytics.AppContext.APPVIEW, action, true, isApkfy);
    } else {
      downloadAnalytics.installClicked(download.getMd5(), download.getVersionCode(),
          download.getPackageName(), trustedValue, editorsChoice, mapDownloadAction(downloadAction),
          action, download.hasAppc(), download.hasSplits(), storeName, isApkfy,
          download.hasObbs(), splitTypes, isInCatappult, appCategory);
      downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
          DownloadAnalytics.AppContext.APPVIEW, action, false, isApkfy);
    }
  }

  private InstallType mapDownloadAction(DownloadModel.Action downloadAction) {
    InstallType installType = InstallType.INSTALL;
    switch (downloadAction) {
      case DOWNGRADE:
        installType = InstallType.DOWNGRADE;
        break;
      case INSTALL:
        installType = InstallType.INSTALL;
        break;
      case UPDATE:
        installType = InstallType.UPDATE;
        break;
      case MIGRATE:
      case OPEN:
        throw new IllegalStateException(
            "Mapping an invalid download action " + downloadAction.name());
    }
    return installType;
  }

  public void sendDownloadPauseEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "pause");
  }

  public void sendDownloadCancelEvent(String packageName) {
    downloadAnalytics.downloadInteractEvent(packageName, "cancel");
  }

  public void sendStoreOpenEvent(Store store) {
    storeAnalytics.sendStoreOpenEvent("App View", store.getName(), true);
  }

  public void similarAppBundleImpression(ApplicationAd.Network network, boolean isAd) {
    similarAppInteract(network, IMPRESSION, null, -1, isAd);
  }

  public void similarAppClick(AppViewSimilarAppsAdapter.SimilarAppType type,
      ApplicationAd.Network network, String packageName, int position, boolean isAd) {
    if (type.equals(AppViewSimilarAppsAdapter.SimilarAppType.APPC_SIMILAR_APPS)) {
      similarAppcAppClick(position, packageName);
    } else if (type.equals(AppViewSimilarAppsAdapter.SimilarAppType.SIMILAR_APPS)) {
      similarAppInteract(network, TAP_ON_APP, packageName, position, isAd);
    }
  }

  private void similarAppcAppClick(int position, String packageName) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_APP);
    data.put(PACKAGE_NAME, packageName);
    data.put(POSITION, position);
    analyticsManager.logEvent(data, APPC_SIMILAR_APP_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  private void similarAppInteract(ApplicationAd.Network network, String action, String packageName,
      int position, boolean isAd) {
    Map<String, Object> data = new HashMap<>();
    if (isAd) data.put(NETWORK, network.getName());
    data.put(ACTION, action);
    data.put(IS_AD, isAd ? "true" : "false");
    if (action.equals(TAP_ON_APP)) {
      data.put(PACKAGE_NAME, packageName);
      data.put(POSITION, position);
    }

    analyticsManager.logEvent(data, SIMILAR_APP_INTERACT,
        action.equals(IMPRESSION) ? AnalyticsManager.Action.IMPRESSION
            : AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true));
  }

  public void similarAppcAppBundleImpression() {
    Map<String, Object> data = new HashMap<>();
    data.put(IS_AD, false);
    data.put(ACTION.toLowerCase(), AnalyticsManager.Action.IMPRESSION.name()
        .toLowerCase());
    analyticsManager.logEvent(data, APPC_SIMILAR_APP_INTERACT, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendAppcMigrationAppOpen() {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "open");
    analyticsManager.logEvent(data, BONUS_MIGRATION_APPVIEW, AnalyticsManager.Action.OPEN,
        navigationTracker.getViewName(true));
  }

  private void sendPromotionImpressionEvent(String promotion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, IMPRESSION);

    analyticsManager.logEvent(data, promotion, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionImpression(String promotionId) {
    if (promotionId.equals("BONUS_MIGRATION_19")) {
      sendPromotionImpressionEvent(BONUS_MIGRATION_APPVIEW);
    } else if (promotionId.equals("BONUS_GAME_WALLET_OFFER_19")) {
      sendPromotionImpressionEvent(BONUS_GAME_WALLET_OFFER_19);
    }
  }

  public void sendAppcMigrationUpdateClick() {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "update to appc");
    analyticsManager.logEvent(data, BONUS_MIGRATION_APPVIEW, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendInstallPromotionApp() {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "install appc app");

    analyticsManager.logEvent(data, BONUS_GAME_WALLET_OFFER_19, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  private void sendInstallAppcWalletEvent(String promotion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "install wallet");

    analyticsManager.logEvent(data, promotion, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendInstallAppcWallet(String promotionId) {
    sendInstallAppcWalletEvent(mapToPromotionOffer(promotionId));
  }

  private void sendClickOnNoThanksWalletInstall(String promotion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "no thanks");

    analyticsManager.logEvent(data, promotion, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnNoThanksWallet(String promotionId) {
    sendClickOnNoThanksWalletInstall(mapToPromotionOffer(promotionId));
  }

  private void sendClickOnClaimAppcWalletPromotion(String promotion) {
    Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "claim");

    analyticsManager.logEvent(data, promotion, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnClaimAppViewPromotion(String promotionId) {
    sendClickOnClaimAppcWalletPromotion(mapToPromotionOffer(promotionId));
  }

  private String mapToPromotionOffer(String promotionId) {
    if (promotionId.equals("BONUS_MIGRATION_19")) {
      return BONUS_MIGRATION_APPVIEW;
    } else if (promotionId.equals("BONUS_GAME_WALLET_OFFER_19")) {
      return BONUS_GAME_WALLET_OFFER_19;
    }
    return "N/A";
  }

  public void sendSimilarABTestGroupEvent(boolean isControlGroup) {
    Logger.getInstance()
        .d("AppViewAnalytics", "similar_apps_control_group: " + isControlGroup);
  }

  @NotNull private HashMap<String, Object> getSimilarABTestData(boolean isControlGroup) {
    return getABTestMap(isControlGroup ? "control" : "appc_bundle");
  }

  private HashMap<String, Object> getABTestMap(String assignment) {
    HashMap<String, Object> data = new HashMap<>();
    data.put("group", assignment);
    return data;
  }

  public void sendInvalidAppEventError(String packageName, int versionCode,
      DownloadModel.Action downloadAction, boolean isMigration,
      boolean isAppBundle, boolean hasAppc, String trustedBadge, String storeName, boolean isApkfy,
      Throwable throwable, boolean hasObb, String splitTypes, String appCategory) {
    downloadAnalytics.sendAppNotValidError(packageName, versionCode,
        mapDownloadAction(downloadAction), isMigration, isAppBundle, hasAppc,
        trustedBadge, storeName, isApkfy, throwable, hasObb, splitTypes,
        storeName.equals("catappult"), appCategory);
  }

  public void sendNotEnoughSpaceErrorEvent(String md5) {
    downloadAnalytics.sendNotEnoughSpaceError(md5);
  }
}
