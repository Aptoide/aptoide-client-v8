package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import android.util.Log;
import cm.aptoide.pt.analytics.AnalyticsDataSaver;
import cm.aptoide.pt.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private Map<EventLogger, Collection<String>> eventSenders;
  private AnalyticsDataSaver analyticsDataSaver;

  private AnalyticsManager(Map<EventLogger, Collection<String>> eventSenders,
      AnalyticsDataSaver analyticsDataSaver) {
    this.eventSenders = eventSenders;
    this.analyticsDataSaver = analyticsDataSaver;
  }

  public void logEvent(Map<String, Object> data, String eventName, Action action, String context) {
    int eventsSent = 0;
    for (Map.Entry<EventLogger, Collection<String>> senderEntry : eventSenders.entrySet()) {
      if (senderEntry.getValue()
          .contains(eventName)) {
        senderEntry.getKey()
            .log(eventName, data, action, context);
        eventsSent++;
      }
    }

    if (eventsSent <= 0) {
      Logger.w(TAG, eventName + " event not sent ");
    }
  }

  public void logEvent(Event event){
    logEvent(event.getData(),event.getEventName(), event.getAction(),event.getContext());
  }

  public void logEvent(String url){
    Log.d("TAG",url);
  }

  public void save(@NonNull String key, @NonNull Event event){
    analyticsDataSaver.save(key,event);
  }

  public void sendAndRemoveEvent(String eventName){
    Event event = analyticsDataSaver.newGet(eventName);
    logEvent(event.getData(),event.getEventName(), event.getAction(), event.getContext());
  }

  public Event getEvent(String key){
    return analyticsDataSaver.newGet(key);
  }

  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, DISMISS
  }

  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventSenders;
    private AnalyticsDataSaver analyticsDataSaver;

    public Builder() {
      eventSenders = new HashMap<>();
    }

    public Builder addLogger(EventLogger eventLogger, Collection<String> supportedEvents) {
      eventSenders.put(eventLogger, supportedEvents);
      return this;
    }

    public Builder addDataSaver(AnalyticsDataSaver analyticsDataSaver) {
      this.analyticsDataSaver=analyticsDataSaver;
      return this;
    }

    public AnalyticsManager build() {
      return new AnalyticsManager(eventSenders, analyticsDataSaver);
    }
  }
}
