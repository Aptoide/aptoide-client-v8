package cm.aptoide.pt.download;

import cm.aptoide.analytics.AnalyticsManager;

public class AppValidationAnalytics {

  public final static String INVALID_DOWNLOAD_PATH_EVENT = "Invalid_Download_Path";
  private final AnalyticsManager analyticsManager;

  public AppValidationAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }
}
