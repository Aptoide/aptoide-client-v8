package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class TimelineClickEvent extends Event {
  public static final String SOURCE_APTOIDE = "Aptoide";
  public static final String OPEN_ARTICLE = "Open_Article";
  public static final String OPEN_BLOG = "Open_Blog";
  public static final String OPEN_VIDEO = "Open_Video";
  public static final String OPEN_CHANNEL = "Open_Channel";
  public static final String OPEN_STORE = "Open_Store";
  public static final String OPEN_APP = "Open_App";
  public static final String UPDATE_APP = "Update_App";
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
