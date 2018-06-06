package cm.aptoide.analytics.implementation;

import android.content.SharedPreferences;

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
