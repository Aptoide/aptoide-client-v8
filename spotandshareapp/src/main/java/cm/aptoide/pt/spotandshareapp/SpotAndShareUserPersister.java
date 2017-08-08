package cm.aptoide.pt.spotandshareapp;

import android.content.SharedPreferences;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserPersister {

  public static final String SHARED_PREFERENCES_NAME = "cm.aptoide.pt";
  public static final String SPOTANDSHARE_ACCOUNT_USERNAME = "SPOTANDSHARE_ACCOUNT_USERNAME";
  public static final String SPOTANDSHARE_ACCOUNT_AVATAR = "SPOTANDSHARE_ACCOUNT_AVATAR";
  private SharedPreferences sharedPreferences;

  public SpotAndShareUserPersister(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public void save(SpotAndShareUser user) {
    sharedPreferences.edit()
        .putString(SPOTANDSHARE_ACCOUNT_USERNAME, user.getUsername())
        .apply();
    sharedPreferences.edit()
        .putInt(SPOTANDSHARE_ACCOUNT_AVATAR, user.getAvatar()
            .getAvatarId())
        .apply();
  }

  public SpotAndShareUser get() {
    String username = sharedPreferences.getString(SPOTANDSHARE_ACCOUNT_USERNAME, "userwithnoname");
    int avatar = sharedPreferences.getInt(SPOTANDSHARE_ACCOUNT_AVATAR, 0);
    return new SpotAndShareUser(username, new SpotAndShareUserAvatar(avatar, ""));
  }
}
