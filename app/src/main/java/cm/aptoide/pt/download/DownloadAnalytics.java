package cm.aptoide.pt.download;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Result;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 04/01/2017.
 */

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.Analytics {
  public static final String DOWNLOAD_EVENT = "Download_99percent";
  private static final String ATTRIBUTE = "APK";
  private Analytics analytics;
  private DownloadCompleteAnalytics downloadCompleteAnalytics;
  private NavigationTracker navigationTracker;
  private AnalyticsManager analyticsManager;

  public DownloadAnalytics(Analytics analytics, DownloadCompleteAnalytics downloadCompleteAnalytics,
      NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.analytics = analytics;
    this.downloadCompleteAnalytics = downloadCompleteAnalytics;
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  @Override public void onError(Download download, Throwable throwable) {
    DownloadEvent report =
        (DownloadEvent) analytics.get(download.getPackageName() + download.getVersionCode(),
            DownloadEvent.class);
    if (report != null) {
      report.setResultStatus(Result.ResultStatus.FAIL);
      report.setError(throwable);
      analytics.sendEvent(report);
    }
  }

  @Override public void onDownloadComplete(Download download) {
    downloadCompleteAnalytics.downloadCompleted(download.getMd5());
  }

  public void moveFile(String movetype) {
    Map<String, Object> map = new HashMap<>();
    map.put(ATTRIBUTE, movetype);
    analyticsManager.logEvent(map, DOWNLOAD_EVENT, AnalyticsManager.Action.AUTO,
        navigationTracker.getViewName(false, "Download"));
  }
}
