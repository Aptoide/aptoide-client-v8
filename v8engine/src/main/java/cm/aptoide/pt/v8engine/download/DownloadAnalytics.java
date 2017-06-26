package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Result;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * Created by trinkes on 04/01/2017.
 */

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.Analytics {
  private Analytics analytics;

  public DownloadAnalytics(Analytics analytics) {
    this.analytics = analytics;
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
}
