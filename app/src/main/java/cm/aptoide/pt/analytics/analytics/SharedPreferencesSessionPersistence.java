package cm.aptoide.pt.analytics.analytics;

import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;

public class SharedPreferencesSessionPersistence implements SessionPersistence {

  private final SharedPreferences sharedPreferences;

  public SharedPreferencesSessionPersistence(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void saveSessionTimestamp(long timestamp) {
    ManagerPreferences.saveSessionTimestamp(timestamp, sharedPreferences);
  }

  @Override public long getTimestamp() {
    return ManagerPreferences.getSessionTimestamp(sharedPreferences);
  }
}
