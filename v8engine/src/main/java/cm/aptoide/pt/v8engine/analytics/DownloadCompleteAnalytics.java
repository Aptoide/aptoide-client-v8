package cm.aptoide.pt.v8engine.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.v8engine.analytics.events.FabricEvent;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 09/08/2017.
 */
public class DownloadCompleteAnalytics {

  public static final String EVENT_NAME = "Download Complete";
  private static final String PARTIAL_EVENT_NAME = "Editors Choice_Download_Complete";

  private static final String PACKAGE_NAME = "Package Name";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private static final String SOURCE = "Source";
  private Analytics analytics;
  private Answers fabric;
  private AppEventsLogger facebookLogger;

  public DownloadCompleteAnalytics(Analytics analytics, Answers fabric,
      AppEventsLogger facebookLogger) {
    this.analytics = analytics;
    this.fabric = fabric;
    this.facebookLogger = facebookLogger;
  }

  public void installClicked(String id, String packageName, String trustedValue) {
    createEvents(id, packageName, trustedValue);
  }

  @NonNull private Bundle mapToBundle(Map<String, String> map) {
    Bundle parameters = new Bundle();
    if (map != null) {
      for (String s : map.keySet()) {
        parameters.putString(s, map.get(s));
      }
    }
    return parameters;
  }

  public void downloadCompleted(String id) {
    sendEvent(analytics.getFlurryEvent(id + PARTIAL_EVENT_NAME));
    sendEvent(analytics.getFabricEvent(id + EVENT_NAME));
    sendEvent(analytics.getFlurryEvent(id + EVENT_NAME));
    sendEvent(analytics.getFacebookEvent(id + EVENT_NAME));
  }

  private void sendEvent(Event event) {
    if (event != null) {
      analytics.sendEvent(event);
    }
  }

  private void createEvents(String id, String packageName, String trustedValue) {

    String lastStep = Analytics.AppViewViewedFrom.getLastStep();
    if (TextUtils.isEmpty(lastStep)) {
      return;
    } else if (lastStep.contains("editor") && lastStep.contains("choice")) {
      HashMap<String, String> map = new HashMap<>();
      map.put(PACKAGE_NAME, packageName);
      FlurryEvent editorsEvent = new FlurryEvent(PARTIAL_EVENT_NAME, map);
      analytics.save(id + PARTIAL_EVENT_NAME, editorsEvent);
    }

    HashMap<String, String> downloadMap = new HashMap<>();
    downloadMap.put(SOURCE, lastStep);
    downloadMap.put(PACKAGE_NAME, packageName);
    downloadMap.put(TRUSTED_BADGE, trustedValue);

    FacebookEvent downloadFacebookEvent =
        new FacebookEvent(facebookLogger, EVENT_NAME, mapToBundle(downloadMap));

    FlurryEvent downloadFlurryEvent = new FlurryEvent(EVENT_NAME, downloadMap);
    FabricEvent downloadFabricEvent = new FabricEvent(fabric, EVENT_NAME, downloadMap);

    analytics.save(id + EVENT_NAME, downloadFabricEvent);
    analytics.save(id + EVENT_NAME, downloadFlurryEvent);
    analytics.save(id + EVENT_NAME, downloadFacebookEvent);
  }
}
