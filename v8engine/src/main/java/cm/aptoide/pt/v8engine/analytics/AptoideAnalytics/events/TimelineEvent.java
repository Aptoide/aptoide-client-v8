package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class TimelineEvent implements Event {
  private final String eventName;
  private final AptoideAccountManager accountManager;
  private final String uniqueIdentifier;

  private AnalyticsEventRequest.Body.Data data;
  private String source;
  private String cardType;
  private AnalyticsEventRequest.Body.Specific specificData;

  public TimelineEvent(String source, String cardType, String eventName,
      AptoideAccountManager accountManager, String uniqueIdentifier,
      AnalyticsEventRequest.Body.Specific specific) {
    this.source = source;
    this.cardType = cardType;
    this.eventName = eventName;
    this.accountManager = accountManager;
    this.uniqueIdentifier = uniqueIdentifier;
    this.specificData = specific;
  }

  @Override public void send() {
    AnalyticsEventRequest.of(accountManager.getAccessToken(),
        AnalyticsEventRequest.Body.Data.builder()
            .cardType(cardType)
            .source(source)
            .specific(specificData)
            .build(), eventName, uniqueIdentifier)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> {
        }, throwable -> throwable.printStackTrace());
  }
}
