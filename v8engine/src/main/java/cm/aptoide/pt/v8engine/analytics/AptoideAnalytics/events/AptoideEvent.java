package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.v8engine.BaseBodyDecorator;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import java.util.Map;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class AptoideEvent implements Event {

  private final Map<String, Object> data;
  private final String eventName;
  private final String action;
  private final String context;
  private BodyDecorator bodyDecorator;

  public AptoideEvent(Map<String, Object> data, String eventName, String action, String context,
      BodyDecorator bodyDecorator) {
    this.data = data;
    this.eventName = eventName;
    this.action = action;
    this.context = context;
    this.bodyDecorator = bodyDecorator;
  }

  @Override public void send() {
    AnalyticsEventRequest.of(eventName, context,
        action, data, bodyDecorator).observe().observeOn(Schedulers.io()).subscribe(baseV7Response -> {
    }, throwable -> throwable.printStackTrace());
  }
}