package cm.aptoide.accountmanager;

import cm.aptoide.pt.preferences.secure.SecurePreferences;

/**
 * Created by trinkes on 5/2/16.
 */
public class AccountManagerPreferences {

  static String getRepoTheme() {
    return SecurePreferences.getString(SecureKeys.REPO_THEME);
  }

  static void setRepoTheme(String repoTheme) {
    SecurePreferences.putString(SecureKeys.REPO_THEME, repoTheme);
  }
}
