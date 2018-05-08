package cm.aptoide.pt.appview;

import android.content.SharedPreferences;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class UserPreferencesPersister {

  private SharedPreferences sharedPreferences;

  public UserPreferencesPersister(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public void save(String key, int value) {
    sharedPreferences.edit()
        .putInt(key, value)
        .apply();
  }

  public int get(String key, int defaultValue) {
    return sharedPreferences.getInt(key, defaultValue);
  }
}
