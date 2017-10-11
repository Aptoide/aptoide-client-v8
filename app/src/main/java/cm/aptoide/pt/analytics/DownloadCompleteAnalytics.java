package cm.aptoide.pt.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.events.FabricEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import cm.aptoide.pt.crashreports.CrashReport;
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
  private CrashReport crashReport;

  public DownloadCompleteAnalytics(Analytics analytics, Answers fabric,
      AppEventsLogger facebookLogger, CrashReport crashReport) {
    this.analytics = analytics;
    this.fabric = fabric;
    this.facebookLogger = facebookLogger;
    this.crashReport = crashReport;
  }

  public void installClicked(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, String editorsBrickPosition) {
    try {
      createEvents(previousScreen, currentScreen, id, packageName, trustedValue,
          editorsBrickPosition);
    } catch (NullPointerException e) {
      crashReport.log(this.getClass()
              .getSimpleName(),
          "Null pointer while trying to get current or previous screen from AptoideNavigation Tracker");
    }
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
    sendEvent(analytics.getFacebookEvent(id + PARTIAL_EVENT_NAME));
    sendEvent(analytics.getFabricEvent(id + EVENT_NAME));
    sendEvent(analytics.getFlurryEvent(id + EVENT_NAME));
    sendEvent(analytics.getFacebookEvent(id + EVENT_NAME));
  }

  private void sendEvent(Event event) {
    if (event != null) {
      analytics.sendEvent(event);
    }
  }

  private void createEvents(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, String editorsChoiceBrickPosition)
      throws NullPointerException {

    if (editorsChoiceBrickPosition != null) {
      HashMap<String, String> map = new HashMap<>();
      map.put(PACKAGE_NAME, packageName);
      map.put("fragment", previousScreen.getFragment());
      map.put("position", editorsChoiceBrickPosition);
      FlurryEvent editorsEvent = new FlurryEvent(PARTIAL_EVENT_NAME, map);
      FacebookEvent editorsChoiceFacebookEvent =
          new FacebookEvent(facebookLogger, PARTIAL_EVENT_NAME, mapToBundle(map));
      analytics.save(id + PARTIAL_EVENT_NAME, editorsEvent);
      analytics.save(id + PARTIAL_EVENT_NAME, editorsChoiceFacebookEvent);
    }

    HashMap<String, String> downloadMap = new HashMap<>();
    downloadMap.put(PACKAGE_NAME, packageName);
    downloadMap.put(TRUSTED_BADGE, trustedValue);
    downloadMap.put("fragment", previousScreen.getFragment());
    downloadMap.put("tag", currentScreen.getTag());
    downloadMap.put("store", previousScreen.getStore());

    FacebookEvent downloadFacebookEvent =
        new FacebookEvent(facebookLogger, EVENT_NAME, mapToBundle(downloadMap));

    FlurryEvent downloadFlurryEvent = new FlurryEvent(EVENT_NAME, downloadMap);
    FabricEvent downloadFabricEvent = new FabricEvent(fabric, EVENT_NAME, downloadMap);

    analytics.save(id + EVENT_NAME, downloadFabricEvent);
    analytics.save(id + EVENT_NAME, downloadFlurryEvent);
    analytics.save(id + EVENT_NAME, downloadFacebookEvent);
  }
}
