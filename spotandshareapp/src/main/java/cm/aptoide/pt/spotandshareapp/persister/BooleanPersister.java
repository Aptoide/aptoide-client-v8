package cm.aptoide.pt.spotandshareapp.persister;

import android.content.SharedPreferences;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.Persister;

/**
 * Created by neuro on 28-06-2017.
 */

public class BooleanPersister implements Persister<String, Boolean> {

  private final SharedPreferences sharedPreferences;

  public BooleanPersister(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void save(String key, Boolean value) {
    sharedPreferences.edit()
        .putBoolean(key, value)
        .apply();
  }

  @Override public Boolean load(String key) {
    return sharedPreferences.getBoolean(key, false);
  }
}
