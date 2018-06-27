package cm.aptoide.analytics.implementation.persistence;

import android.content.SharedPreferences;
import cm.aptoide.analytics.implementation.SessionPersistence;

public class SharedPreferencesSessionPersistence implements SessionPersistence {

  private final SharedPreferences sharedPreferences;

  public SharedPreferencesSessionPersistence(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void saveSessionTimestamp(long timestamp) {
    sharedPreferences.edit()
        .putLong("session_timestamp", timestamp)
        .apply();
  }

  @Override public long getTimestamp() {
    return sharedPreferences.getLong("session_timestamp", 0);
  }
}
