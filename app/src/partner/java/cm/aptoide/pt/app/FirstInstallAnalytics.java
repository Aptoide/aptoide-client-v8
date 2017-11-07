package cm.aptoide.pt.app;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by diogoloureiro on 25/10/2017.
 *
 * First install analytics class implementation
 */

public class FirstInstallAnalytics {

  private static final String FIRST_INSTALL_POP_UP = "First_Install_Pop_up";
  private static final String FIRST_INSTALL_CLOSE_WINDOW = "First_Install_Close_Window";
  private static final String FIRST_INSTALL_START_DOWNLOAD = "First_Install_Start_Download";
  private static final String FIRST_INSTALL_SPONSORED_APPS_SELECTED = "sponsored_apps_selected";
  private static final String FIRST_INSTALL_NORMAL_APPS_SELECTED = "normal_apps_selected";

  private Analytics analytics;
  private AppEventsLogger facebook;

  public FirstInstallAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendPopupEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, FIRST_INSTALL_POP_UP));
    analytics.sendEvent(new FlurryEvent(FIRST_INSTALL_POP_UP));
  }

  public void sendCloseWindowsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, FIRST_INSTALL_CLOSE_WINDOW));
    analytics.sendEvent(new FlurryEvent(FIRST_INSTALL_CLOSE_WINDOW));
  }

  public void sendStartDownloadEvent(String sponsored, String normal) {
    analytics.sendEvent(new FacebookEvent(facebook, FIRST_INSTALL_START_DOWNLOAD,
        createStartDownloadBundle(sponsored, normal)));
    analytics.sendEvent(
        new FlurryEvent(FIRST_INSTALL_START_DOWNLOAD, createStartDownloadMap(sponsored, normal)));
  }

  private Bundle createStartDownloadBundle(String sponsored, String normal) {
    Bundle bundle = new Bundle();
    bundle.putString(FIRST_INSTALL_SPONSORED_APPS_SELECTED, sponsored);
    bundle.putString(FIRST_INSTALL_NORMAL_APPS_SELECTED, normal);
    return bundle;
  }

  private Map<String, String> createStartDownloadMap(String sponsored, String normal) {
    HashMap<String, String> map = new HashMap<>();
    map.put(FIRST_INSTALL_SPONSORED_APPS_SELECTED, sponsored);
    map.put(FIRST_INSTALL_NORMAL_APPS_SELECTED, normal);
    return map;
  }
}
