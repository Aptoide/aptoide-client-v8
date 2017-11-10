package cm.aptoide.pt.preferences;

import android.content.SharedPreferences;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * Partners Secure Preferences implementation
 */

public class PartnersSecurePreferences {

  /**
   * @return get remote boot config JSON string
   */
  public static String getRemoteBootConfigJSONString(SharedPreferences sharedPreferences) {
    return sharedPreferences.getString(PartnersSecureKeys.JSON_PREFERENCE_STRING, "");
  }

  /**
   * @param remoteBootConfigJSONString set remote boot config JSON string
   */
  public static void setRemoteBootConfigJSONString(String remoteBootConfigJSONString,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putString(PartnersSecureKeys.JSON_PREFERENCE_STRING, remoteBootConfigJSONString)
        .apply();
  }

  /**
   * @return true if the first install was already interacted with
   */
  public static boolean isFirstInstallFinished(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(PartnersSecureKeys.FIRST_INSTALL_ALREADY_APPEARED, false);
  }

  /**
   * @param isFirstInstallFinished set boolean if the first install was already interact with or
   * not
   */
  public static void setFirstInstallFinished(boolean isFirstInstallFinished,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(PartnersSecureKeys.FIRST_INSTALL_ALREADY_APPEARED, isFirstInstallFinished)
        .apply();
  }
}