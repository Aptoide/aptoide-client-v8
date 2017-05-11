package cm.aptoide.pt.v8engine.pull;

import android.content.SharedPreferences;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationStatusManager {
  public static final String NOTIFICATION_STATUS_KEY = "NOTIFICATION_STATUS";
  private final SharedPreferences sharedPreferences;

  public NotificationStatusManager(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  void setVisible(int notificationId, boolean isVisible) {
    sharedPreferences.edit()
        .putBoolean(NOTIFICATION_STATUS_KEY + String.valueOf(notificationId), isVisible)
        .apply();
  }

  boolean isVisible(int notificationId) {
    return sharedPreferences.getBoolean(NOTIFICATION_STATUS_KEY + String.valueOf(notificationId),
        false);
  }
}