package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class TimelineClickEvent extends Event {
  public static final String SOURCE_APTOIDE = "APTOIDE";
  public static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  public static final String OPEN_BLOG = "OPEN_BLOG";
  public static final String OPEN_VIDEO = "OPEN_VIDEO";
  public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  public static final String OPEN_STORE = "OPEN_STORE";
  public static final String OPEN_APP = "OPEN_APP";
  public static final String UPDATE_APP = "UPDATE_APP";
  private final SendEventRequest.Body.Data data;
  private final String eventName;

  public TimelineClickEvent(SendEventRequest.Body.Data data, String eventName) {
    this.data = data;
    this.eventName = eventName;
  }

  @Override public void send() {
    SendEventRequest.of(AptoideAccountManager.getAccessToken(), data, eventName)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> {
        }, throwable -> throwable.printStackTrace());
  }
}
