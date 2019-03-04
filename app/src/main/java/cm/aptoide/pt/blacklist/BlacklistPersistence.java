package cm.aptoide.pt.blacklist;

import android.content.SharedPreferences;

public class BlacklistPersistence {

  private SharedPreferences sharedPreferences;

  public BlacklistPersistence(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public boolean isBlacklisted(String id, int maxPossibleImpressions) {
    return sharedPreferences.getInt(id, 0) == maxPossibleImpressions;
  }

  public void addImpression(String id, int maxImpressions) {
    int actualImpressions = sharedPreferences.getInt(id, maxImpressions);
    if (actualImpressions >= 0) {
      actualImpressions--;
      sharedPreferences.edit()
          .putInt(id, actualImpressions)
          .apply();
    }
  }

  public void blacklist(String id, int maxPossibleImpressions) {
    sharedPreferences.edit()
        .putInt(id, maxPossibleImpressions)
        .apply();
  }
}
