package cm.aptoide.pt.download;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.analytics.AppSizeAnalyticsStringMapper;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.DeepLinkManager;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.DownloadAnalytics {
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
  private static final String APP_OBB = "app_obb";
  private static final String ERROR_TYPE = "error_type";
  private static final String ERROR_MESSAGE = "error_message";
  private static final String ERROR_URL = "error_url";
  private static final String ERROR_HTTP_CODE = "error_http_code";
  private static final String IS_APKFY = "apkfy_app_install";
  private static final String MIUI_AAB_FIX = "miui_aab_fix";
  private static final String APP_VERSION_CODE = "app_version_code";
  private static final String APP_AAB_INSTALL_TIME = "app_aab_install_time";
  private static final String APP_IN_CATAPPULT = "app_in_catappult";
  public static final String GAMES_CATEGORY = "games";
  private static final String APP_IS_GAME = "app_is_game";
  private static final String DOWNLOAD_SPEED_MBPS = "download_speed_mbps";
  private static final String APP_SIZE_MB = "app_size_mb";
  private final Map<String, DownloadEvent> cache;
  private final ConnectivityManager connectivityManager;
  private final TelephonyManager telephonyManager;
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;
  private final AppSizeAnalyticsStringMapper appSizeAnalyticsStringMapper;

  public DownloadAnalytics(ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager, NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager,
      AppSizeAnalyticsStringMapper appSizeAnalyticsStringMapper) {
    this.appSizeAnalyticsStringMapper = appSizeAnalyticsStringMapper;
    this.cache = new HashMap<>();
    this.connectivityManager = connectivityManager;
    this.telephonyManager = telephonyManager;
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  @Override public void onDownloadComplete(String md5, String packageName, int versionCode,
      int averageDownloadSpeed) {
    sendDownloadEvent(md5 + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME);
    sendDownloadEvent(md5 + DOWNLOAD_COMPLETE_EVENT);
    sendDownloadEvent(md5 + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME);
    sendRakamDownloadEvent(md5 + RAKAM_DOWNLOAD_EVENT, averageDownloadSpeed);
  }

  @Override
  public void onError(String packageName, int versionCode, String md5, Throwable throwable,
      String downloadErrorUrl, String downloadHttpError, int averageDownloadSpeed) {
    handleRakamOnError(md5, throwable, downloadErrorUrl, downloadHttpError, averageDownloadSpeed);
  }

  @Override public void startProgress(RoomDownload download) {
    updateDownloadEventWithHasProgress(download.getMd5() + DOWNLOAD_COMPLETE_EVENT);
    updateDownloadEventWithHasProgress(
        download.getMd5() + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME);
    updateDownloadEventWithHasProgress(download.getMd5() + RAKAM_DOWNLOAD_EVENT);
  }

  private void sendRakamDownloadEvent(String downloadCacheKey, int averageDownloadSpeed) {
    double averageDownloadSpeedMbps = (double) averageDownloadSpeed / 1024;
    DownloadEvent downloadEvent = cache.get(downloadCacheKey);
    if (downloadEvent != null && downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      data.put(STATUS, "success");
      data.put(DOWNLOAD_SPEED_MBPS, round(averageDownloadSpeedMbps * 100.0) / 100.0);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(downloadCacheKey);
    }
  }

  private void handleRakamOnError(String md5, Throwable throwable, String downloadErrorUrl,
      String downloadHttpError, int averageDownloadSpeed) {
    double averageDownloadSpeedMbps = (double) averageDownloadSpeed / 1024;
    DownloadEvent downloadEvent = cache.get(md5 + RAKAM_DOWNLOAD_EVENT);
    if (downloadEvent != null) {
      Map<String, Object> data = downloadEvent.getData();
      data.put(STATUS, "fail");
      data.put(ERROR_TYPE, throwable.getClass()
          .getSimpleName());
      data.put(ERROR_MESSAGE, throwable.getMessage());
      data.put(ERROR_URL, downloadErrorUrl);
      data.put(ERROR_HTTP_CODE, downloadHttpError);
      data.put(DOWNLOAD_SPEED_MBPS, round(averageDownloadSpeedMbps * 100.0) / 100.0);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(md5 + RAKAM_DOWNLOAD_EVENT);
      sendSentryError(throwable, downloadErrorUrl, downloadHttpError);
    }
  }

  private void sendSentryError(Throwable throwable, String downloadErrorUrl,
      String downloadHttpError) {
    SentryEvent sentryEvent = new SentryEvent();
    Message message = new Message();
    message.setMessage("download_fail");
    sentryEvent.setMessage(message);
    sentryEvent.setExtra(ERROR_TYPE, throwable.getClass()
        .getSimpleName());
    sentryEvent.setExtra(ERROR_MESSAGE, throwable.getMessage());
    sentryEvent.setExtra(ERROR_URL, downloadErrorUrl);
    sentryEvent.setExtra(ERROR_HTTP_CODE, downloadHttpError);
    Sentry.captureEvent(sentryEvent);
  }

  public void sendNotEnoughSpaceError(String md5) {
    DownloadEvent downloadEvent = cache.get(md5 + RAKAM_DOWNLOAD_EVENT);
    if (downloadEvent != null) {
      Map<String, Object> result = downloadEvent.getData();
      result.put(STATUS, "incomplete");
      result.put(ERROR_TYPE, "FileDownloadOutOfSpace");
      analyticsManager.logEvent(result, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
    }
  }

  public void sendAppNotValidError(String packageName, int versionCode, InstallType installType,
      boolean isMigration, boolean isAppBundle, boolean hasAppc, String trustedBadge,
      String storeName, boolean isApkfy,
      Throwable throwable, boolean hasObb, String splitTypes, boolean isInCatappult,
      String appCategory, long appSize) {

    String previousContext = navigationTracker.getPreviousViewName();
    String context = navigationTracker.getCurrentViewName();
    String tag = navigationTracker.getCurrentScreen() != null ? navigationTracker.getCurrentScreen()
        .getTag() : "";

    HashMap<String, Object> result =
        createRakamDownloadEvent(packageName, versionCode, installType.toString(), isMigration,
            isAppBundle, hasAppc, trustedBadge, storeName,
            isApkfy, previousContext, context, tag, hasObb, splitTypes, isInCatappult, appCategory,
            appSize);

    result.put(STATUS, "fail");
    result.put(ERROR_TYPE, throwable.getClass()
        .getSimpleName());
    result.put(ERROR_MESSAGE, throwable.getMessage());

    DownloadEvent downloadEvent =
        new DownloadEvent(RAKAM_DOWNLOAD_EVENT, result, context, AnalyticsManager.Action.CLICK);

    analyticsManager.logEvent(result, downloadEvent.getEventName(), downloadEvent.getAction(),
        downloadEvent.getContext());
  }

  private void sendDownloadEvent(String downloadCacheKey) {
    DownloadEvent downloadEvent = cache.get(downloadCacheKey);
    if (downloadEvent != null && downloadEvent.isHadProgress()) {
      analyticsManager.logEvent(downloadEvent.getData(), downloadEvent.getEventName(),
          downloadEvent.getAction(), downloadEvent.getContext());
      cache.remove(downloadCacheKey);
    }
  }

  public Origin getOrigin(int downloadAction) {
    Origin origin;
    switch (downloadAction) {
      case RoomDownload.ACTION_INSTALL:
        origin = Origin.INSTALL;
        break;
      case RoomDownload.ACTION_UPDATE:
        origin = Origin.UPDATE;
        break;
      case RoomDownload.ACTION_DOWNGRADE:
        origin = Origin.DOWNGRADE;
        break;
      default:
        origin = Origin.INSTALL;
    }
    return origin;
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

  public void installClicked(String md5, int versionCode, String packageName, String trustedValue,
      String editorsBrickPosition, InstallType installType, AnalyticsManager.Action action,
      boolean hasAppc, boolean isAppBundle, String storeName, boolean isApkfy, boolean hasObbs,
      String splitTypes, boolean isInCatappult, String appCategory, long appSize) {
    setUpInstallEvent(md5, versionCode, packageName, trustedValue, editorsBrickPosition,
        installType, action, false, hasAppc, isAppBundle, storeName, isApkfy,
        hasObbs, splitTypes, isInCatappult, appCategory, appSize);
  }

  public void migrationClicked(String md5, int versionCode, String packageName, String trustedValue,
      String editorsBrickPosition, InstallType installType, AnalyticsManager.Action action,
      boolean hasAppc, boolean isAppBundle, String storeName, boolean isApkfy, boolean hasObb,
      String splitTypes, boolean isInCatappult, String appCategory, long appSize) {
    setUpInstallEvent(md5, versionCode, packageName, trustedValue, editorsBrickPosition,
        installType, action, true, hasAppc, isAppBundle, storeName, isApkfy,
        hasObb, splitTypes, isInCatappult, appCategory, appSize);
  }

  public void migrationClicked(String md5, String packageName, int versionCode,
      AnalyticsManager.Action action, boolean isAppBundle, String trustedBadge, String tag,
      String storeName, boolean hasObbs,
      String splitTypes, boolean isInCatappult, String appCategory, long appSize) {
    setUpInstallEvent(md5, packageName, versionCode, action, true, true,
        isAppBundle, trustedBadge, storeName, "update_to_appc", hasObbs, splitTypes,
        isInCatappult, appCategory, appSize);
  }

  private void setUpInstallEvent(String md5, int versionCode, String packageName,
      String trustedValue, String editorsBrickPosition, InstallType installType,
      AnalyticsManager.Action action,
      boolean isMigration, boolean hasAppc, boolean isAppBundle, String storeName, boolean isApkfy,
      boolean hasObbs, String splitTypes, boolean isInCatappult, String appCategory, long appSize) {
    String currentContext = navigationTracker.getViewName(true);

    rakamDownloadCompleteEvent(md5, packageName, versionCode, installType.toString(), isMigration,
        isAppBundle, hasAppc, trustedValue, storeName, isApkfy,
        hasObbs, splitTypes, isInCatappult, appCategory, appSize);
    editorsChoiceDownloadCompletedEvent(currentContext, md5, packageName, editorsBrickPosition,
        installType, currentContext, action, hasAppc, isAppBundle, isApkfy);
    pushNotificationDownloadEvent(currentContext, md5, packageName, installType, action,
        currentContext, isApkfy);
    downloadCompleteEvent(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), md5, packageName, trustedValue, action,
        currentContext, isMigration, hasAppc, isAppBundle, isApkfy);
  }

  public void installClicked(String md5, String packageName, int versionCode,
      AnalyticsManager.Action action, boolean isMigration, boolean hasAppc, boolean isAppBundle,
      String trustedBadge, String tag, String storeName, String installType, boolean hasObb,
      String splitTypes, boolean isInCatappult, String appCategory, long appSize) {
    setUpInstallEvent(md5, packageName, versionCode, action, isMigration,
        hasAppc, isAppBundle, trustedBadge, storeName, installType, hasObb, splitTypes,
        isInCatappult, appCategory, appSize);
  }

  private void setUpInstallEvent(String md5, String packageName, int versionCode,
      AnalyticsManager.Action action, boolean isMigration, boolean hasAppc, boolean isAppBundle,
      String trustedBadge, String storeName, String installType, boolean hasObbs, String splitTypes,
      boolean isInCatappult, String appCategory, long appSize) {
    String currentContext = navigationTracker.getViewName(true);

    rakamDownloadCompleteEvent(md5, packageName, versionCode, installType,
        isMigration, isAppBundle, hasAppc, trustedBadge, storeName, false, hasObbs, splitTypes,
        isInCatappult, appCategory, appSize);
    downloadCompleteEvent(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), md5, packageName, null, action, currentContext,
        isMigration, hasAppc, isAppBundle, false);
  }

  private void rakamDownloadCompleteEvent(String md5, String packageName, int versionCode,
      String action,
      boolean isMigration, boolean isAppBundle, boolean hasAppc, String trustedBadge,
      String storeName, boolean isApkfy, boolean hasObb, String splitTypes, boolean isInCatappult,
      String appCategory, long appSize) {
    String previousContext = navigationTracker.getPreviousViewName();
    String context = navigationTracker.getCurrentViewName();
    String tag = navigationTracker.getCurrentScreen() != null ? navigationTracker.getCurrentScreen()
        .getTag() : "";

    HashMap<String, Object> result =
        createRakamDownloadEvent(packageName, versionCode, action, isMigration,
            isAppBundle, hasAppc, trustedBadge, storeName, isApkfy, previousContext, context, tag,
            hasObb, splitTypes, isInCatappult, appCategory, appSize);

    DownloadEvent downloadEvent =
        new DownloadEvent(RAKAM_DOWNLOAD_EVENT, result, context, AnalyticsManager.Action.CLICK);
    cache.put(md5 + RAKAM_DOWNLOAD_EVENT, downloadEvent);
  }

  private HashMap<String, Object> createRakamDownloadEvent(String packageName, int versionCode,
      String action,
      boolean isMigration, boolean isAppBundle, boolean hasAppc, String trustedBadge,
      String storeName, boolean isApkfy, String previousContext, String context, String tag,
      boolean hasObbs, String splitTypes, boolean isInCatappult, String appCategory, long appSize) {

    HashMap<String, Object> result = new HashMap<>();
    result.put(CONTEXT, context);
    result.put(ACTION, action.toLowerCase());
    result.put(PACKAGE_NAME, packageName);
    result.put(APP_VERSION_CODE, versionCode);
    result.put(PREVIOUS_CONTEXT, previousContext);
    result.put(APP_MIGRATION, isMigration);
    result.put(APP_APPC, hasAppc);
    result.put(APP_AAB, isAppBundle);
    result.put(APP_OBB, hasObbs);
    result.put(IS_APKFY, isApkfy);
    result.put(APP_AAB_INSTALL_TIME, splitTypes);
    result.put(APP_IN_CATAPPULT, isInCatappult);
    if (!appCategory.isEmpty()) {
      result.put(APP_IS_GAME, appCategory.equals(GAMES_CATEGORY));
    }
    if (trustedBadge != null) result.put(TRUSTED_BADGE, trustedBadge.toLowerCase());
    if (!tag.isEmpty()) {
      result.put(TAG, tag);
    }
    result.put(STORE, storeName);
    result.put(APP_SIZE_MB, appSizeAnalyticsStringMapper.mapAppSizeToMBBucketValue(appSize));
    return result;
  }

  public void downloadCompleteEvent(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, AnalyticsManager.Action action,
      String context, boolean areAdsBlockedByOffer, boolean isMigration, boolean hasAppc,
      boolean isAppBundle, boolean isApkfy) {
    HashMap<String, Object> downloadMap =
        createDownloadCompleteEventMap(previousScreen, currentScreen, packageName, trustedValue,
            isMigration, hasAppc, isAppBundle, context, isApkfy);
    downloadMap.put(ADS_BLOCK_BY_OFFER, areAdsBlockedByOffer);
    DownloadEvent downloadEvent =
        new DownloadEvent(DOWNLOAD_COMPLETE_EVENT, downloadMap, context, action);
    cache.put(id + DOWNLOAD_COMPLETE_EVENT, downloadEvent);
  }

  public void downloadCompleteEvent(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, AnalyticsManager.Action action,
      String context, boolean isMigration, Boolean hasAppc, boolean isAppBundle, boolean isApkfy) {
    DownloadEvent downloadEvent = new DownloadEvent(DOWNLOAD_COMPLETE_EVENT,
        createDownloadCompleteEventMap(previousScreen, currentScreen, packageName, trustedValue,
            isMigration, hasAppc, isAppBundle, context, isApkfy), context, action);
    cache.put(id + DOWNLOAD_COMPLETE_EVENT, downloadEvent);
  }

  @NonNull
  private HashMap<String, Object> createDownloadCompleteEventMap(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String packageName, String trustedValue, boolean isMigration,
      boolean hasAppc, boolean isAppBundle, String context, boolean isApkfy) {
    HashMap<String, Object> downloadMap = new HashMap<>();
    downloadMap.put(PACKAGENAME, packageName);
    downloadMap.put(CONTEXT, context);
    downloadMap.put(TRUSTED_BADGE, trustedValue);
    downloadMap.put(APPC, hasAppc);
    downloadMap.put(APP_BUNDLE, isAppBundle);
    downloadMap.put(MIGRATOR, isMigration);
    downloadMap.put(IS_APKFY, isApkfy);
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
      InstallType installType, AnalyticsManager.Action action, String currentContext,
      boolean isApkfy) {
    if (previousScreen.equals(DeepLinkManager.DEEPLINK_KEY)) {
      HashMap<String, Object> data = new HashMap();
      data.put(PACKAGENAME, packageName);
      data.put(TYPE, installType.name());
      data.put(IS_APKFY, isApkfy);

      DownloadEvent downloadEvent =
          new DownloadEvent(NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, data, currentContext,
              action);
      cache.put(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, downloadEvent);
    }
  }

  private void editorsChoiceDownloadCompletedEvent(String previousScreen, String id,
      String packageName, String editorsBrickPosition, InstallType installType, String context,
      AnalyticsManager.Action action, boolean hasAppc, boolean isAppBundle, boolean isApkfy) {
    if (editorsBrickPosition != null && !editorsBrickPosition.isEmpty()) {
      HashMap<String, Object> map = new HashMap<>();
      map.put(PACKAGENAME, packageName);
      map.put(CONTEXT, context);
      map.put(PREVIOUS_CONTEXT, previousScreen);
      map.put(POSITION, editorsBrickPosition);
      map.put(TYPE, installType.name());
      map.put(APPC, hasAppc);
      map.put(APP_BUNDLE, isAppBundle);
      map.put(IS_APKFY, isApkfy);
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
      AnalyticsManager.Action action,
      boolean isAppBundle) {

    String currentContext = navigationTracker.getViewName(true);
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    ScreenTagHistory currentScreen = navigationTracker.getCurrentScreen();

    downloadCompleteEvent(previousScreen, currentScreen, id, packageName, trustedValue, action,
        currentContext, false, false, isAppBundle, false);
  }

  public enum AppContext {
    TIMELINE, APPVIEW, UPDATE_TAB, APPS_FRAGMENT, APPS_MIGRATOR_SEE_MORE, AUTO_UPDATE, DOWNLOADS, EDITORIAL, PROMOTIONS, WALLET_INSTALL_ACTIVITY, SEARCH
  }

  public static class DownloadEvent {
    private final Map<String, Object> data;
    private final String eventName;
    private final AnalyticsManager.Action action;
    private final String context;
    private boolean hadProgress;

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
