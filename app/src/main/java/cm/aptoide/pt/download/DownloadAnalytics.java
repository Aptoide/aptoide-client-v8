package cm.aptoide.pt.download;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.DeepLinkManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.DownloadAnalytics {
  public static final String DOWNLOAD_EVENT_NAME = "DOWNLOAD";
  public static final String NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Aptoide_Push_Notification_Download_Complete";
  public static final String DOWNLOAD_COMPLETE_EVENT = "Download Complete";
  public static final String EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Editors_Choice_Download_Complete";
  public static final String DOWNLOAD_INTERACT = "Download_Interact";
  public static final String RAKAM_DOWNLOAD_EVENT = "download";
  private static final String UPDATE_TO_APPC = "UPDATE TO APPC";
  private static final String AB_TEST_GROUP = "ab_test_group";
  private static final String ACTION = "action";
  private static final String APP = "app";
  private static final String APPC = "appc";
  private static final String APP_BUNDLE = "app_bundle";
  private static final String CAMPAIGN_ID = "campaign_id";
  private static final String FAIL = "FAIL";
  private static final String ERROR = "error";
  private static final String STATUS = "status";
  private static final String TYPE = "type";
  private static final String MAIN = "MAIN";
  private static final String MIGRATOR = "migrator";
  private static final String MESSAGE = "message";
  private static final String MIRROR = "mirror";
  private static final String NETWORK = "network";
  private static final String OBB = "obb";
  private static final String ORIGIN = "origin";
  private static final String PACKAGE = "package";
  private static final String PACKAGENAME = "Package Name";
  private static final String PACKAGE_NAME = "package_name";
  private static final String CONTEXT = "context";
  private static final String PATCH = "PATCH";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String PREVIOUS_TAG = "previous_tag";
  private static final String POSITION = "position";
  private static final String RESULT = "result";
  private static final String STORE = "store";
  private static final String SUCCESS = "SUCC";
  private static final String TAG = "tag";
  private static final String TELECO = "teleco";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private static final String URL = "url";
  private static final String ADS_BLOCK_BY_OFFER = "ads_block_by_offer";
  private static final String APP_MIGRATION = "app_migration";
  private static final String APP_APPC = "app_appc";
  private static final String APP_AAB = "app_aab";
  private static final String ADS_BLOCKED = "ads_status";
  private static final String ERROR_TYPE = "error_type";
  private static final String ERROR_MESSAGE = "error_message";
  private final Map<String, DownloadEvent> cache;
  private final ConnectivityManager connectivityManager;
  private final TelephonyManager telephonyManager;
  private NavigationTracker navigationTracker;
  private AnalyticsManager analyticsManager;

  public DownloadAnalytics(ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager, NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager) {
    this.cache = new HashMap<>();
    this.connectivityManager = connectivityManager;
    this.telephonyManager = telephonyManager;
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  @Override public void onDownloadComplete(String md5, String packageName, int versionCode) {
    sendDownloadCompletedEvent(packageName, versionCode);
    sendDownloadEvent(md5 + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME);
    sendDownloadEvent(md5 + DOWNLOAD_COMPLETE_EVENT);
    sendDownloadEvent(md5 + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME);
    sendRakamDownloadEvent(md5 + RAKAM_DOWNLOAD_EVENT);
  }

  @Override
  public void onError(String packageName, int versionCode, String md5, Throwable throwable) {
    String key = packageName + versionCode + DOWNLOAD_EVENT_NAME;
    handleRakamOnError(md5, throwable);
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent != null) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      Map<String, Object> error = new HashMap<>();

      result.put(STATUS, FAIL);
      error.put(TYPE, throwable.getClass()
          .getSimpleName());
      error.put(MESSAGE, throwable.getMessage());
      result.put(ERROR, error);
      data.put(RESULT, result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(key);
    }
  }

  private void sendRakamDownloadEvent(String downloadCacheKey) {
    DownloadEvent downloadEvent = cache.get(downloadCacheKey);
    if (downloadEvent != null && downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      data.put(STATUS, "success");
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(downloadCacheKey);
    }
  }

  private void handleRakamOnError(String md5, Throwable throwable) {
    DownloadEvent downloadEvent = cache.get(md5 + RAKAM_DOWNLOAD_EVENT);
    if (downloadEvent != null) {
      Map<String, Object> data = downloadEvent.getData();
      data.put(STATUS, "fail");
      data.put(ERROR_TYPE, throwable.getClass()
          .getSimpleName());
      data.put(ERROR_MESSAGE, throwable.getMessage());
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(md5 + RAKAM_DOWNLOAD_EVENT);
    }
  }

  private void sendDownloadCompletedEvent(Download download) {
    String key = download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME;
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      result.put(STATUS, SUCCESS);
      data.put(RESULT, result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(key);
    }
  }

  private void sendDownloadCompletedEvent(String packageName, int versionCode) {
    String key = packageName + versionCode + DOWNLOAD_EVENT_NAME;
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      result.put(STATUS, SUCCESS);
      data.put(RESULT, result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(key);
    }
  }

  private void sendDownloadEvent(String downloadCacheKey) {
    DownloadEvent downloadEvent = cache.get(downloadCacheKey);
    if (downloadEvent != null && downloadEvent.isHadProgress()) {
      analyticsManager.logEvent(downloadEvent.getData(), downloadEvent.getEventName(),
          downloadEvent.getAction(), downloadEvent.getContext());
      cache.remove(downloadCacheKey);
    }
  }

  public void downloadStartEvent(Download download, AnalyticsManager.Action action,
      AppContext context, Boolean isMigration) {
    downloadStartEvent(download, 0, null, context, action, isMigration, getOrigin(download));
  }

  public void downloadStartEvent(Download download, AnalyticsManager.Action action,
      AppContext context, Boolean isMigration, Origin origin) {
    downloadStartEvent(download, 0, null, context, action, isMigration, origin);
  }

  public void downloadStartEvent(Download download, int campaignId, String abTestGroup,
      AppContext context, AnalyticsManager.Action action, boolean isMigration) {
    downloadStartEvent(download, campaignId, abTestGroup, context, action, isMigration,
        getOrigin(download));
  }

  public void downloadStartEvent(Download download, int campaignId, String abTestGroup,
      AppContext context, AnalyticsManager.Action action, boolean isMigration, Origin origin) {
    Map<String, Object> event = new HashMap<>();
    ScreenTagHistory screenTagHistory = navigationTracker.getPreviousScreen();
    event.put(APP, createAppData(download));
    event.put(NETWORK, AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    if (isMigration) {
      event.put(ORIGIN, UPDATE_TO_APPC);
    } else {
      event.put(ORIGIN, origin);
    }
    event.put(PREVIOUS_CONTEXT, screenTagHistory.getFragment());
    event.put(TAG, navigationTracker.getCurrentScreen()
        .getTag());

    event.put(STORE, navigationTracker.getPreviousScreen()
        .getStore());
    event.put(TELECO, AptoideUtils.SystemU.getCarrierName(telephonyManager));
    event.put(MIGRATOR, isMigration);

    if (campaignId > 0) {
      event.put(CAMPAIGN_ID, campaignId);
      event.put(AB_TEST_GROUP, abTestGroup);
    }

    cache.put(download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME,
        new DownloadEvent(DOWNLOAD_EVENT_NAME, event, context, action));
  }

  @NonNull private Map<String, Object> createAppData(Download download) {
    Map<String, Object> app = new HashMap<>();
    app.put(PACKAGE, download.getPackageName());
    app.put(APPC, download.hasAppc());
    app.put(APP_BUNDLE, download.hasSplits());
    return app;
  }

  public Origin getOrigin(Download download) {
    Origin origin;
    switch (download.getAction()) {
      case Download.ACTION_INSTALL:
        origin = Origin.INSTALL;
        break;
      case Download.ACTION_UPDATE:
        origin = Origin.UPDATE;
        break;
      case Download.ACTION_DOWNGRADE:
        origin = Origin.DOWNGRADE;
        break;
      default:
        origin = Origin.INSTALL;
    }
    return origin;
  }

  public void updateDownloadEvent(String versionCode, String packageName, int fileType,
      String mirror, String url) {
    Map<String, Object> event = cache.get(packageName + versionCode + DOWNLOAD_EVENT_NAME)
        .getData();
    if (event != null) {
      if (fileType == 0) {
        Map<String, Object> app = (Map<String, Object>) event.get("app");
        app.put(MIRROR, mirror);
        app.put(URL, url);
      } else {
        List<Map<String, Object>> obb = (List<Map<String, Object>>) event.get("obb");
        if (obb == null) {
          obb = new ArrayList<>();
        }
        obb.add(createObbData(fileType, url, mirror));
        event.put(OBB, obb);
      }
    }
  }

  private Map<String, Object> createObbData(int fileType, String url, String mirror) {
    Map<String, Object> obb = new HashMap<>();
    if (fileType == 1) {
      obb.put(MIRROR, mirror);
      obb.put(TYPE, MAIN);
    } else if (fileType == 2) {
      obb.put(MIRROR, mirror);
      obb.put(TYPE, PATCH);
    }
    obb.put(URL, url);
    return obb;
  }

  public void startProgress(Download download) {
    updateDownloadEventWithHasProgress(
        download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME);
    updateDownloadEventWithHasProgress(download.getMd5() + DOWNLOAD_COMPLETE_EVENT);
    updateDownloadEventWithHasProgress(
        download.getMd5() + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME);
    updateDownloadEventWithHasProgress(download.getMd5() + RAKAM_DOWNLOAD_EVENT);
  }

  private void updateDownloadEventWithHasProgress(String key) {
    DownloadEvent event = cache.get(key);
    if (event != null) {
      event.setHadProgress(true);
    } else {
      Logger.getInstance()
          .d("DownloadAnalytics", "Tried setting progress on an event that was not setup " + key);
    }
  }

  public void installClicked(String md5, String packageName, String trustedValue,
      String editorsBrickPosition, InstallType installType, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean hasAppc,
      boolean isAppBundle, String storeName) {
    setUpInstallEvent(md5, packageName, trustedValue, editorsBrickPosition, installType, action,
        offerResponseStatus, false, hasAppc, isAppBundle, storeName);
  }

  public void migrationClicked(String md5, String packageName, String trustedValue,
      String editorsBrickPosition, InstallType installType, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean hasAppc,
      boolean isAppBundle, String storeName) {
    setUpInstallEvent(md5, packageName, trustedValue, editorsBrickPosition, installType, action,
        offerResponseStatus, true, hasAppc, isAppBundle, storeName);
  }

  public void migrationClicked(String md5, String packageName, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean isAppBundle,
      String trustedBadge, String tag, String storeName) {
    setUpInstallEvent(md5, packageName, action, offerResponseStatus, true, true, isAppBundle,
        trustedBadge, storeName, "update_to_appc");
  }

  private void setUpInstallEvent(String md5, String packageName, String trustedValue,
      String editorsBrickPosition, InstallType installType, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, String storeName) {
    String currentContext = navigationTracker.getViewName(true);

    rakamDownloadCompleteEvent(md5, packageName, installType.toString(), offerResponseStatus,
        isMigration, isAppBundle, hasAppc, trustedValue, storeName);
    editorsChoiceDownloadCompletedEvent(currentContext, md5, packageName, editorsBrickPosition,
        installType, currentContext, action, hasAppc, isAppBundle);
    pushNotificationDownloadEvent(currentContext, md5, packageName, installType, action,
        currentContext);
    if (!offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.NO_ADS)) {

      downloadCompleteEvent(navigationTracker.getPreviousScreen(),
          navigationTracker.getCurrentScreen(), md5, packageName, trustedValue, action,
          currentContext,
          offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE),
          isMigration, hasAppc, isAppBundle);
    } else {
      downloadCompleteEvent(navigationTracker.getPreviousScreen(),
          navigationTracker.getCurrentScreen(), md5, packageName, trustedValue, action,
          currentContext, isMigration, hasAppc, isAppBundle);
    }
  }

  public void installClicked(String md5, String packageName, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, String trustedBadge, String tag, String storeName,
      String installType) {
    setUpInstallEvent(md5, packageName, action, offerResponseStatus, isMigration, hasAppc,
        isAppBundle, trustedBadge, storeName, installType);
  }

  private void setUpInstallEvent(String md5, String packageName, AnalyticsManager.Action action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, String trustedBadge, String storeName,
      String installType) {
    String currentContext = navigationTracker.getViewName(true);

    rakamDownloadCompleteEvent(md5, packageName, installType, offerResponseStatus, isMigration,
        isAppBundle, hasAppc, trustedBadge, storeName);

    if (!offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.NO_ADS)) {

      downloadCompleteEvent(navigationTracker.getPreviousScreen(),
          navigationTracker.getCurrentScreen(), md5, packageName, null, action, currentContext,
          offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE),
          isMigration, hasAppc, isAppBundle);
    } else {
      downloadCompleteEvent(navigationTracker.getPreviousScreen(),
          navigationTracker.getCurrentScreen(), md5, packageName, null, action, currentContext,
          isMigration, hasAppc, isAppBundle);
    }
  }

  private void rakamDownloadCompleteEvent(String md5, String packageName, String action,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean isMigration,
      boolean isAppBundle, boolean hasAppc, String trustedBadge, String storeName) {
    String previousContext = navigationTracker.getPreviousViewName();
    String context = navigationTracker.getCurrentViewName();
    String tag_ =
        navigationTracker.getCurrentScreen() != null ? navigationTracker.getCurrentScreen()
            .getTag() : "";

    HashMap<String, Object> result = new HashMap<>();
    result.put(CONTEXT, context);
    result.put(ACTION, action.toLowerCase());
    result.put(PACKAGE_NAME, packageName);
    result.put(PREVIOUS_CONTEXT, previousContext);
    result.put(APP_MIGRATION, isMigration);
    result.put(APP_APPC, hasAppc);
    result.put(APP_AAB, isAppBundle);
    if (trustedBadge != null) result.put(TRUSTED_BADGE, trustedBadge.toLowerCase());
    result.put(ADS_BLOCKED, offerResponseStatus.toString()
        .toLowerCase());
    if (!tag_.isEmpty()) {
      result.put(TAG, tag_);
    }
    result.put(STORE, storeName);

    DownloadEvent downloadEvent =
        new DownloadEvent(RAKAM_DOWNLOAD_EVENT, result, context, AnalyticsManager.Action.CLICK);
    cache.put(md5 + RAKAM_DOWNLOAD_EVENT, downloadEvent);
  }

  public void downloadCompleteEvent(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, AnalyticsManager.Action action,
      String context, boolean areAdsBlockedByOffer, boolean isMigration, boolean hasAppc,
      boolean isAppBundle) {
    HashMap<String, Object> downloadMap =
        createDownloadCompleteEventMap(previousScreen, currentScreen, packageName, trustedValue,
            isMigration, hasAppc, isAppBundle, context);
    downloadMap.put(ADS_BLOCK_BY_OFFER, areAdsBlockedByOffer);
    DownloadEvent downloadEvent =
        new DownloadEvent(DOWNLOAD_COMPLETE_EVENT, downloadMap, context, action);
    cache.put(id + DOWNLOAD_COMPLETE_EVENT, downloadEvent);
  }

  public void downloadCompleteEvent(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, AnalyticsManager.Action action,
      String context, boolean isMigration, Boolean hasAppc, boolean isAppBundle) {
    DownloadEvent downloadEvent = new DownloadEvent(DOWNLOAD_COMPLETE_EVENT,
        createDownloadCompleteEventMap(previousScreen, currentScreen, packageName, trustedValue,
            isMigration, hasAppc, isAppBundle, context), context, action);
    cache.put(id + DOWNLOAD_COMPLETE_EVENT, downloadEvent);
  }

  @NonNull
  private HashMap<String, Object> createDownloadCompleteEventMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String trustedValue, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, String context) {
    HashMap<String, Object> downloadMap = new HashMap<>();
    downloadMap.put(PACKAGENAME, packageName);
    downloadMap.put(CONTEXT, context);
    downloadMap.put(TRUSTED_BADGE, trustedValue);
    downloadMap.put(APPC, hasAppc);
    downloadMap.put(APP_BUNDLE, isAppBundle);
    downloadMap.put(MIGRATOR, isMigration);
    if (previousScreen != null) {
      downloadMap.put(TAG, currentScreen.getTag());
      if (previousScreen.getFragment() != null) {
        downloadMap.put(PREVIOUS_CONTEXT, previousScreen.getFragment());
      }
      if (previousScreen.getStore() != null) {
        downloadMap.put(STORE, previousScreen.getStore());
      }
    }
    return downloadMap;
  }

  private void pushNotificationDownloadEvent(String previousScreen, String id, String packageName,
      InstallType installType, AnalyticsManager.Action action, String currentContext) {
    if (previousScreen.equals(DeepLinkManager.DEEPLINK_KEY)) {
      HashMap<String, Object> data = new HashMap();
      data.put(PACKAGENAME, packageName);
      data.put(TYPE, installType.name());

      DownloadEvent downloadEvent =
          new DownloadEvent(NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, data, currentContext,
              action);
      cache.put(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, downloadEvent);
    }
  }

  private void editorsChoiceDownloadCompletedEvent(String previousScreen, String id,
      String packageName, String editorsBrickPosition, InstallType installType, String context,
      AnalyticsManager.Action action, boolean hasAppc, boolean isAppBundle) {
    if (editorsBrickPosition != null && !editorsBrickPosition.isEmpty()) {
      HashMap<String, Object> map = new HashMap<>();
      map.put(PACKAGENAME, packageName);
      map.put(CONTEXT, context);
      map.put(PREVIOUS_CONTEXT, previousScreen);
      map.put(POSITION, editorsBrickPosition);
      map.put(TYPE, installType.name());
      map.put(APPC, hasAppc);
      map.put(APP_BUNDLE, isAppBundle);
      DownloadEvent downloadEvent =
          new DownloadEvent(EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME, map, context, action);
      cache.put(id + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME, downloadEvent);
    }
  }

  public void downloadInteractEvent(String packageName, String action) {
    final HashMap<String, Object> data = new HashMap<>();
    data.put(PACKAGE_NAME, packageName);
    data.put(ACTION, action);

    analyticsManager.logEvent(data, DOWNLOAD_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void downloadCompleteEvent(String id, String packageName, String trustedValue,
      AnalyticsManager.Action action, WalletAdsOfferManager.OfferResponseStatus offerResponseStatus,
      boolean isAppBundle) {

    String currentContext = navigationTracker.getViewName(true);
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreen = navigationTracker.getCurrentScreen();

    if (!offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.NO_ADS)) {

      downloadCompleteEvent(previousScreen, currentScreen, id, packageName, trustedValue, action,
          currentContext,
          offerResponseStatus.equals(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE), false,
          isAppBundle);
    } else {
      downloadCompleteEvent(previousScreen, currentScreen, id, packageName, trustedValue, action,
          currentContext, false, false, isAppBundle);
    }
  }

  public enum AppContext {
    TIMELINE, APPVIEW, UPDATE_TAB, APPS_FRAGMENT, APPS_MIGRATOR_SEE_MORE, AUTO_UPDATE, DOWNLOADS, EDITORIAL, PROMOTIONS, WALLET_INSTALL_ACTIVITY
  }

  public static class DownloadEvent {
    private final Map<String, Object> data;
    private final String eventName;
    private final AnalyticsManager.Action action;
    private final String context;
    private boolean hadProgress;

    private DownloadEvent(String eventName, Map<String, Object> data, AppContext context,
        AnalyticsManager.Action action) {
      this.data = data;
      this.eventName = eventName;
      this.action = action;
      this.context = context.name();
      hadProgress = false;
    }

    public DownloadEvent(String eventName, HashMap<String, Object> data, String context,
        AnalyticsManager.Action action) {
      this.data = data;
      this.eventName = eventName;
      this.action = action;
      this.context = context;
      hadProgress = false;
    }

    public boolean isHadProgress() {
      return hadProgress;
    }

    public void setHadProgress(boolean hadProgress) {
      this.hadProgress = hadProgress;
    }

    public Map<String, Object> getData() {
      return data;
    }

    public String getEventName() {
      return eventName;
    }

    public AnalyticsManager.Action getAction() {
      return action;
    }

    public String getContext() {
      return context;
    }
  }
}
