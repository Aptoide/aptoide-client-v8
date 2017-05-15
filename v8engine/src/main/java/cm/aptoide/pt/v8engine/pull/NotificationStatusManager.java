package cm.aptoide.pt.v8engine.pull;

import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import java.util.Set;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationStatusManager {
  public static final String NOTIFICATION_STATUS_IDS_KEY = "NOTIFICATION_STATUS_IDS";
  public static final String NOTIFICATION_STATUS_KEY = "NOTIFICATION_STATUS";
  private final SharedPreferences sharedPreferences;

  public NotificationStatusManager(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  void setVisible(int notificationId, boolean isVisible) {
    String key = NOTIFICATION_STATUS_KEY + String.valueOf(notificationId);
    addId(key);
    sharedPreferences.edit()
        .putBoolean(key, isVisible)
        .apply();
  }

  private void addId(String key) {
    Set<String> ids = getIds();
    ids.add(key);
    sharedPreferences.edit()
        .putStringSet(NOTIFICATION_STATUS_IDS_KEY, ids)
        .apply();
  }

  boolean isVisible(int notificationId) {
    return sharedPreferences.getBoolean(NOTIFICATION_STATUS_KEY + String.valueOf(notificationId),
        false);
  }

  void reset() {
    SharedPreferences.Editor edit = sharedPreferences.edit();
    for (String id : getIds()) {
      edit.putBoolean(id, false);
    }
    edit.apply();
  }

  private Set<String> getIds() {
    return sharedPreferences.getStringSet(NOTIFICATION_STATUS_IDS_KEY, new ArraySet<>());
  }
}