package cm.aptoide.pt.download;

import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.Analytics {
  public static final String DOWNLOAD_EVENT = "Download_99percent";
  public static final String DOWNLOAD_EVENT_NAME = "DOWNLOAD";
  private final Map<String, DownloadEvent> cache;
  private final ConnectivityManager connectivityManager;
  private final TelephonyManager telephonyManager;
  private final DownloadCompleteAnalytics downloadCompleteAnalytics;
  private NavigationTracker navigationTracker;
  private AnalyticsManager analyticsManager;

  public DownloadAnalytics(ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager, DownloadCompleteAnalytics downloadCompleteAnalytics,
      NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.cache = new HashMap<>();
    this.connectivityManager = connectivityManager;
    this.telephonyManager = telephonyManager;
    this.downloadCompleteAnalytics = downloadCompleteAnalytics;
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
          downloadEvent.getContext()
              .name());
      cache.remove(key);
    }
  }

  @Override public void onDownloadComplete(Download download) {
    String key = download.getPackageName() + download.getVersionCode() + DOWNLOAD_EVENT_NAME;
    DownloadEvent downloadEvent = cache.get(key);
    if (downloadEvent.isHadProgress()) {
      Map<String, Object> data = downloadEvent.getData();
      Map<String, Object> result = new HashMap<>();
      result.put("status", "SUCC");
      data.put("result", result);
      analyticsManager.logEvent(data, downloadEvent.getEventName(), downloadEvent.getAction(),
          downloadEvent.getContext()
              .name());
      cache.remove(key);
    }
    downloadCompleteAnalytics.downloadCompleted(download.getMd5());
  }

  public void moveFile(String movetype) {
    Map<String, Object> map = new HashMap<>();
    map.put("APK", movetype);
    analyticsManager.logEvent(map, DOWNLOAD_EVENT, AnalyticsManager.Action.AUTO,
        navigationTracker.getViewName(false, "Download"));
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

  public enum AppContext {
    TIMELINE, APPVIEW, UPDATE_TAB, SCHEDULED, DOWNLOADS
  }

  public enum Origin {
    INSTALL, UPDATE, DOWNGRADE, UPDATE_ALL
  }

  public static class DownloadEvent {
    private final Map<String, Object> data;
    private final String eventName;
    private final AnalyticsManager.Action action;
    private final AppContext context;
    private boolean hadProgress;

    private DownloadEvent(String eventName, Map<String, Object> data, AppContext context,
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

    public AppContext getContext() {
      return context;
    }
  }
}
