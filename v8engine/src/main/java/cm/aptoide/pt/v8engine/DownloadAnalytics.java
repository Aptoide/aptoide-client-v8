package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports.DownloadEvent;

/**
 * Created by trinkes on 04/01/2017.
 */

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.interfaces.Analytics {
  private Analytics analytics;

  public DownloadAnalytics(Analytics analytics) {
    this.analytics = analytics;
  }

  @Override public void onError(Download download, Throwable throwable) {
    DownloadEvent report =
        (DownloadEvent) analytics.get(download.getPackageName() + download.getVersionCode());
    if (report != null) {
      report.setResultStatus(DownloadInstallAnalyticsBaseBody.ResultStatus.FAIL);
      report.setError(throwable);
      analytics.sendEvent(report);
    }
  }
}
