package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import java.util.Map;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class AptoideEvent implements Event {

  private final AptoideAccountManager accountManager;
  private final String uniqueIdentifier;
  private final Map<String, Object> data;
  private final String eventName;
  private final String action;
  private final String context;

  public AptoideEvent(AptoideAccountManager accountManager, String uniqueIdentifier,
      Map<String, Object> data, String eventName, String action, String context) {
    this.accountManager = accountManager;
    this.uniqueIdentifier = uniqueIdentifier;
    this.data = data;
    this.eventName = eventName;
    this.action = action;
    this.context = context;
  }

  @Override public void send() {
    AnalyticsEventRequest.of(accountManager.getAccessToken(), eventName, uniqueIdentifier, context,
        action, data).observe().observeOn(Schedulers.io()).subscribe(baseV7Response -> {
    }, throwable -> throwable.printStackTrace());
  }
}