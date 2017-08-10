package cm.aptoide.pt.spotandshareapp;

import android.content.SharedPreferences;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserPersister {

  public static final String DEFAULT_USERNAME = "userwithnoname";
  public static final int DEFAULT_AVATAR_ID = 0;
  public static final String DEFAULT_AVATAR_PATH =
      "android.resource://spotandshareapp.dev/drawable/spotandshare_avatar_01";

  public static final String SHARED_PREFERENCES_NAME = "cm.aptoide.pt";
  public static final String SPOTANDSHARE_ACCOUNT_USERNAME = "SPOTANDSHARE_ACCOUNT_USERNAME";
  public static final String SPOTANDSHARE_ACCOUNT_AVATAR_ID = "SPOTANDSHARE_ACCOUNT_AVATAR_ID";
  public static final String SPOTANDSHARE_ACCOUNT_AVATAR_PATH = "SPOTANDSHARE_ACCOUNT_AVATAR_PATH";
  private SharedPreferences sharedPreferences;

  public SpotAndShareUserPersister(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public void save(SpotAndShareUser user) {
    sharedPreferences.edit()
        .putString(SPOTANDSHARE_ACCOUNT_USERNAME, user.getUsername())
        .apply();
    sharedPreferences.edit()
        .putInt(SPOTANDSHARE_ACCOUNT_AVATAR_ID, user.getAvatar()
            .getAvatarId())
        .apply();
    sharedPreferences.edit()
        .putString(SPOTANDSHARE_ACCOUNT_AVATAR_PATH, user.getAvatar()
            .getString())
        .apply();
  }

  public SpotAndShareUser get() {
    String username = sharedPreferences.getString(SPOTANDSHARE_ACCOUNT_USERNAME, DEFAULT_USERNAME);
    int avatarId = sharedPreferences.getInt(SPOTANDSHARE_ACCOUNT_AVATAR_ID, DEFAULT_AVATAR_ID);
    String avatarPath =
        sharedPreferences.getString(SPOTANDSHARE_ACCOUNT_AVATAR_PATH, DEFAULT_AVATAR_PATH);
    return new SpotAndShareUser(username, new SpotAndShareUserAvatar(avatarId, avatarPath, false));
  }
}
