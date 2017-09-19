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
}