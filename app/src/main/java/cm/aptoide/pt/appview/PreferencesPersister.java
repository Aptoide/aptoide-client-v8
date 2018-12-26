package cm.aptoide.pt.appview;

import android.content.SharedPreferences;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class PreferencesPersister {

  private SharedPreferences sharedPreferences;

  public PreferencesPersister(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public void save(String key, int value) {
    sharedPreferences.edit()
        .putInt(key, value)
        .apply();
  }

  public void save(String key, boolean value) {
    sharedPreferences.edit()
        .putBoolean(key, value)
        .apply();
  }

  public int get(String key, int defaultValue) {
    return sharedPreferences.getInt(key, defaultValue);
  }

  public boolean get(String key, boolean defaultValue) {
    return sharedPreferences.getBoolean(key, defaultValue);
  }
}
