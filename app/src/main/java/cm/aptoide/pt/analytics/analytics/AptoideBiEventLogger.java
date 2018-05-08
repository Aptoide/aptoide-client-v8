package cm.aptoide.pt.analytics.analytics;

import android.content.SharedPreferences;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class AptoideBiEventLogger implements EventLogger, SessionLogger {
  private static final String TAG = AptoideBiEventLogger.class.getSimpleName();
  private final AptoideBiAnalytics service;

  public AptoideBiEventLogger(AptoideBiAnalytics service) {
    this.service = service;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    service.log(eventName, data, action, context);
  }

  @Override public void setup() {
    service.setup();
  }

  @Override public void startSession() {
    service.getTimestamp(); //todo
    //Logger.d(TAG, "startSession: " + ManagerPreferences.getSessionTimestamp(sharedPreferences));
  //  service.log("SESSION",null, AnalyticsManager.Action.OPEN,"APPLICATION");
  }

  @Override public void endSession() {

  }
}
