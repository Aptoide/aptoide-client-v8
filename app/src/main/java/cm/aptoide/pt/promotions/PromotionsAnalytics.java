package cm.aptoide.pt.promotions;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;

public class PromotionsAnalytics {

  private final DownloadAnalytics downloadAnalytics;

  public PromotionsAnalytics(DownloadAnalytics downloadAnalytics) {
    this.downloadAnalytics = downloadAnalytics;
  }

  public void setupDownloadEvents(Download download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.PROMOTIONS, action);
  }
}
