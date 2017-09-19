package cm.aptoide.pt.notification;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.KnockEvent;
import okhttp3.OkHttpClient;

/**
 * Created by trinkes on 18/09/2017.
 */

public class NotificationAnalytics {

  private final OkHttpClient client;
  private final Analytics analytics;

  public NotificationAnalytics(OkHttpClient client, Analytics analytics) {
    this.client = client;
    this.analytics = analytics;
  }

  public void notificationShown(String url) {
    analytics.sendEvent(new KnockEvent(url, client));
  }
}
