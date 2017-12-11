package cm.aptoide.pt.download;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.Event;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.events.FabricEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import cm.aptoide.pt.view.DeepLinkManager;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

public class DownloadCompleteAnalytics {

  public static final String EVENT_NAME = "Download Complete";
  public static final String INSTALL_TYPE_KEY = "type";
  private static final String PARTIAL_EVENT_NAME = "Editors Choice_Download_Complete";
  private static final String NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Aptoide_Push_Notification_Download_Complete";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private Analytics analytics;
  private Answers fabric;
  private AppEventsLogger facebookLogger;

  public DownloadCompleteAnalytics(Analytics analytics, Answers fabric,
      AppEventsLogger facebookLogger) {
    this.analytics = analytics;
    this.fabric = fabric;
    this.facebookLogger = facebookLogger;
  }

  public void installClicked(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, String editorsBrickPosition,
      InstallType installType) {

    if (editorsBrickPosition != null) {
      HashMap<String, String> map = new HashMap<>();
      map.put(PACKAGE_NAME, packageName);
      if (previousScreen.getFragment() != null) {
        map.put("fragment", previousScreen.getFragment());
      }
      map.put("position", editorsBrickPosition);
      map.put(INSTALL_TYPE_KEY, installType.name());
      FlurryEvent editorsEvent = new FlurryEvent(PARTIAL_EVENT_NAME, map);
      analytics.save(id + PARTIAL_EVENT_NAME, editorsEvent);
    }

    HashMap<String, String> downloadMap = new HashMap<>();
    downloadMap.put(SOURCE, lastStep);
    downloadMap.put(PACKAGE_NAME, packageName);
    downloadMap.put(TRUSTED_BADGE, trustedValue);
    FacebookEvent notificationDownloadComplete = null;
    if (previousScreen != null) {
      if (previousScreen.getFragment()
          .equals(DeepLinkManager.DEEPLINK_KEY)) {
        Bundle data = new Bundle();
        data.putString(PACKAGE_NAME, packageName);
        data.putString(INSTALL_TYPE_KEY, installType.name());
        notificationDownloadComplete =
            new FacebookEvent(facebookLogger, NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, data);
      }
      if (previousScreen.getFragment() != null) {
        downloadMap.put("fragment", previousScreen.getFragment());
      }
      if (previousScreen.getStore() != null) {
        downloadMap.put("store", previousScreen.getStore());
      }
    }
    if (currentScreen != null) {
      if (currentScreen.getTag() != null) {
        downloadMap.put("tag", currentScreen.getTag());
      }
    }

    FacebookEvent downloadFacebookEvent =
        new FacebookEvent(facebookLogger, EVENT_NAME, mapToBundle(downloadMap));

    FlurryEvent downloadFlurryEvent = new FlurryEvent(EVENT_NAME, downloadMap);
    FabricEvent downloadFabricEvent = new FabricEvent(fabric, EVENT_NAME, downloadMap);

    analytics.save(id + EVENT_NAME, downloadFabricEvent);
    analytics.save(id + EVENT_NAME, downloadFlurryEvent);
    analytics.save(id + EVENT_NAME, downloadFacebookEvent);
    if (notificationDownloadComplete != null) {
      analytics.save(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, notificationDownloadComplete);
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
    sendEvent(analytics.getFacebookEvent(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME));
  }

  private void sendEvent(Event event) {
    if (event != null) {
      analytics.sendEvent(event);
    }
  }

  public enum InstallType {
    INSTALL, UPDATE, DOWNGRADE,
  }
}
