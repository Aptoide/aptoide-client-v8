package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;

/**
 * Created by jdandrade on 27/10/2016.
 *
 */
public class TimelineMetricsManager {
  public void sendEvent(SendEventRequest.Body.Data data, String eventName) {
    AptoideAnalytics.logTimelineEvent(data, eventName);
  }
}