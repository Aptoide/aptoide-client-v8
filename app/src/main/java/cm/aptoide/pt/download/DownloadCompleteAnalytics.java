package cm.aptoide.pt.download;

import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.analytics.analytics.Event;
import cm.aptoide.pt.view.DeepLinkManager;
import java.util.HashMap;

public class DownloadCompleteAnalytics {

  public static final String EVENT_NAME = "Download Complete";
  public static final String INSTALL_TYPE_KEY = "type";
  public static final String PARTIAL_EVENT_NAME = "Editors Choice_Download_Complete";
  public static final String NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME =
      "Aptoide_Push_Notification_Download_Complete";
  private static final String PACKAGE_NAME = "Package Name";
  private static final String TRUSTED_BADGE = "Trusted Badge";
  private AnalyticsManager analyticsManager;

  public DownloadCompleteAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void installClicked(ScreenTagHistory previousScreen, ScreenTagHistory currentScreen,
      String id, String packageName, String trustedValue, String editorsBrickPosition,
      InstallType installType, String context) {

    if (editorsBrickPosition != null) {
      HashMap<String, Object> map = new HashMap<>();
      map.put(PACKAGE_NAME, packageName);
      if (previousScreen.getFragment() != null) {
        map.put("fragment", previousScreen.getFragment());
      }
      map.put("position", editorsBrickPosition);
      map.put(INSTALL_TYPE_KEY, installType.name());
      Event event = new Event(PARTIAL_EVENT_NAME, map, AnalyticsManager.Action.CLICK,context);
      analyticsManager.save(id + PARTIAL_EVENT_NAME, event);
    }

    HashMap<String, Object> downloadMap = new HashMap<>();
    downloadMap.put(PACKAGE_NAME, packageName);
    downloadMap.put(TRUSTED_BADGE, trustedValue);
    Event notificationDownloadComplete = null;
    if (previousScreen != null) {
      if (previousScreen.getFragment()
          .equals(DeepLinkManager.DEEPLINK_KEY)) {
        HashMap<String, Object> data = new HashMap();
        data.put(PACKAGE_NAME, packageName);
        data.put(INSTALL_TYPE_KEY, installType.name());
        notificationDownloadComplete =
            new Event(NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, data, AnalyticsManager.Action.AUTO, context);
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

    Event downloadEvent =
        new Event(EVENT_NAME, downloadMap, AnalyticsManager.Action.AUTO,context);


    analyticsManager.save(id + EVENT_NAME, downloadEvent);
    if (notificationDownloadComplete != null) {
      analyticsManager.save(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, notificationDownloadComplete);
    }
  }

  public void downloadCompleted(String id) {
    sendEvent(analyticsManager.getEvent(id + PARTIAL_EVENT_NAME));
    sendEvent(analyticsManager.getEvent(id + EVENT_NAME));
    sendEvent(analyticsManager.getEvent(id + NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME));
  }

  private void sendEvent(Event event) {
    if (event != null) {
      analyticsManager.logEvent(event.getData(),event.getEventName(),event.getAction(),event.getContext());
    }
  }

  public enum InstallType {
    INSTALL, UPDATE, DOWNGRADE,
  }
}
