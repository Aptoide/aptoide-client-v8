package cm.aptoide.pt.download;

import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.DeepLinkManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.Analytics {
  public static final String DOWNLOAD_EVENT = "Download_99percent";
  public static final String DOWNLOAD_EVENT_NAME = "DOWNLOAD";
  public static final String NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Aptoide_Push_Notification_Download_Complete";
  public static final String DOWNLOAD_COMPLETE_EVENT = "Download Complete";
  public static final String EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Editors Choice_Download_Complete";
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

  @Override public void onError(Download download, Throwable throwable) {

    String key = download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME;
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent != null) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      Map<String, Object> error = new HashMap<>();

      result.put("status", "FAIL");
      error.put("type", throwable.getClass()
          .getSimpleName());
      error.put("message", throwable.getMessage());
      result.put("error", error);
      data.put("result", result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(key);
    }
  }

  @Override public void onDownloadComplete(Download download) {
    sendDownloadCompletedEvent(download);
    sendDownloadEvent(download.getMd5() + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME);
    sendDownloadEvent(download.getMd5() + DOWNLOAD_COMPLETE_EVENT);
    sendDownloadEvent(download.getMd5() + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME);
  }

  private void sendDownloadCompletedEvent(Download download) {
    String key = download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME;
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      result.put("status", "SUCC");
      data.put("result", result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext());
      cache.remove(key);
    }
  }

  private void sendDownloadEvent(String downloadCacheKey) {
    DownloadEvent downloadEvent = cache.get(downloadCacheKey);
    if (downloadEvent != null) {
      analyticsManager.logEvent(downloadEvent.getData(), downloadEvent.getEventName(),
          downloadEvent.getAction(), downloadEvent.getContext());
      cache.remove(downloadCacheKey);
    }
  }

  public void moveFile(String movetype) {
    Map<String, Object> map = new HashMap<>();
    map.put("APK", movetype);
    analyticsManager.logEvent(map, DOWNLOAD_EVENT, AnalyticsManager.Action.AUTO,
        navigationTracker.getViewName(false));
  }

  public void downloadStartEvent(Download download, AnalyticsManager.Action action,
      AppContext context) {
    downloadStartEvent(download, 0, null, context, action);
  }

  public void downloadStartEvent(Download download, int campaignId, String abTestGroup,
      AppContext context, AnalyticsManager.Action action) {
    Map<String, Object> event = new HashMap<>();
    event.put("app", createAppData(download));
    event.put("network", AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    event.put("origin", getOrigin(download));
    event.put("previous_context", navigationTracker.getPreviousScreen()
        .getFragment());
    event.put("previous_tag", navigationTracker.getCurrentScreen()
        .getTag());
    event.put("store", navigationTracker.getPreviousScreen()
        .getStore());
    event.put("teleco", AptoideUtils.SystemU.getCarrierName(telephonyManager));

    if (campaignId > 0) {
      event.put("campaign_id", campaignId);
      event.put("ab_testing_group", abTestGroup);
    }

    cache.put(download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME,
        new DownloadEvent(DOWNLOAD_EVENT_NAME, event, context, action));
  }

  @NonNull private Map<String, Object> createAppData(Download download) {
    Map<String, Object> app = new HashMap<>();
    app.put("package", download.getPackageName());
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
        app.put("mirror", mirror);
        app.put("url", url);
      } else {
        List<Map<String, Object>> obb = (List<Map<String, Object>>) event.get("obb");
        if (obb == null) {
          obb = new ArrayList<>();
        }
        obb.add(createObbData(fileType, url, mirror));
        event.put("obb", obb);
      }
    }
  }

  private Map<String, Object> createObbData(int fileType, String url, String mirror) {
    Map<String, Object> obb = new HashMap<>();
    if (fileType == 1) {
      obb.put("mirror", mirror);
      obb.put("type", "MAIN");
    } else if (fileType == 2) {
      obb.put("mirror", mirror);
      obb.put("type", "PATCH");
    }
    obb.put("url", url);
    return obb;
  }

  public void startProgress(Download download) {
    cache.get(download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME)
        .setHadProgress(true);
  }

  public void installClicked(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, String editorsBrickPosition,
      InstallType installType, AnalyticsManager.Action action, String previousContext,
      String currentContext) {
    editorsChoiceDownloadCompletedEvent(previousScreen, id, packageName, editorsBrickPosition,
        installType, currentContext, action);
    pushNotificationDownloadEvent(previousScreen, id, packageName, installType, action,
        currentContext);
    downloadCompleteEvent(previousScreen, currentScreen, id, packageName, trustedValue, action,
        previousContext);
  }

  private void downloadCompleteEvent(ScreenTagHistory previousScreen,
      ScreenTagHistory currentScreen, String id, String packageName, String trustedValue,
      AnalyticsManager.Action action, String previousContext) {
    HashMap<String, Object> downloadMap = new HashMap<>();
    downloadMap.put("Package Name", packageName);
    downloadMap.put("Trusted Badge", trustedValue);
    if (previousScreen != null) {
      downloadMap.put("tag", currentScreen.getTag());
      if (previousScreen.getFragment() != null) {
        downloadMap.put("fragment", previousScreen.getFragment());
      }
      if (previousScreen.getStore() != null) {
        downloadMap.put("store", previousScreen.getStore());
      }
    }
    DownloadEvent downloadEvent =
        new DownloadEvent(DOWNLOAD_COMPLETE_EVENT, downloadMap, previousContext, action);
    cache.put(id + DOWNLOAD_COMPLETE_EVENT, downloadEvent);
  }

  private void pushNotificationDownloadEvent(ScreenTagHistory previousScreen, String id,
      String packageName, InstallType installType, AnalyticsManager.Action action,
      String currentContext) {
    if (previousScreen != null && previousScreen.getFragment()
        .equals(DeepLinkManager.DEEPLINK_KEY)) {
      HashMap<String, Object> data = new HashMap();
      data.put("Package Name", packageName);
      data.put("type", installType.name());

      DownloadEvent downloadEvent =
          new DownloadEvent(NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, data, currentContext,
              action);
      cache.put(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, downloadEvent);
    }
  }

  private void editorsChoiceDownloadCompletedEvent(ScreenTagHistory previousScreen, String id,
      String packageName, String editorsBrickPosition, InstallType installType, String context,
      AnalyticsManager.Action action) {
    if (editorsBrickPosition != null) {
      HashMap<String, Object> map = new HashMap<>();
      map.put("Package Name", packageName);
      if (previousScreen.getFragment() != null) {
        map.put("fragment", previousScreen.getFragment());
      }
      map.put("position", editorsBrickPosition);
      map.put("type", installType.name());
      DownloadEvent downloadEvent =
          new DownloadEvent(EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME, map, context, action);
      cache.put(id + EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME, downloadEvent);
    }
  }

  public enum AppContext {
    TIMELINE, APPVIEW, UPDATE_TAB, DOWNLOADS
  }

  public enum Origin {
    INSTALL, UPDATE, DOWNGRADE, UPDATE_ALL
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
