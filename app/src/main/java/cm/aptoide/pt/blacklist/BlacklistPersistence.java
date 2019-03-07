package cm.aptoide.pt.blacklist;

import android.content.SharedPreferences;

public class BlacklistPersistence {

  private SharedPreferences sharedPreferences;

  public BlacklistPersistence(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public boolean isBlacklisted(String id, int maxPossibleImpressions) {
    return sharedPreferences.getInt(id, maxPossibleImpressions) == 0;
  }

  public void addImpression(String id, int maxPossibleImpressions) {
    int actualImpressions = sharedPreferences.getInt(id, maxPossibleImpressions);
    if (actualImpressions > 0) {
      actualImpressions--;
      sharedPreferences.edit()
          .putInt(id, actualImpressions)
          .apply();
    }
  }

  public void blacklist(String id) {
    sharedPreferences.edit()
        .putInt(id, 0)
        .apply();
  }
}
