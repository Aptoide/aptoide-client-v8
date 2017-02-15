package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineClickEvent;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineMetricsManager {
  private Analytics analytics;

  public TimelineMetricsManager(Analytics analytics) {
    this.analytics = analytics;
  }

  public void sendEvent(SendEventRequest.Body.Data data, String eventName) {
    analytics.sendEvent(new TimelineClickEvent(data, eventName));
  }
}