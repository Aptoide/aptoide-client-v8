package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import lombok.Getter;

/**
 * Created by trinkes on 30/12/2016.
 */

public class AnalyticsDataManager {
  static @Getter AnalyticsDataManager instance = new AnalyticsDataManager(new AnalyticsDataSaver());
  AnalyticsDataSaver saver;

  public AnalyticsDataManager(AnalyticsDataSaver saver) {
    this.saver = saver;
  }

  public void save(String md5, Report report) {
    saver.save(md5, report);
  }

  public Report get(String md5) {
    return saver.get(md5);
  }
}