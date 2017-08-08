package cm.aptoide.pt.preferences;

import android.content.SharedPreferences;

/**
 * Created by diogoloureiro on 24/10/16.
 */

public class PartnersSecurePreferences {

  /**
   * @return true if shorcut is already created, false is not
   */
  public static boolean getIsShortcutCreated(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(PartnersSecureKeys.IS_SHORTCUT_CREATED, false);
  }

  /**
   * @param isCreated set true if the shorcut is created, false if not
   */
  public static void setIsShortcutCreated(boolean isCreated, SharedPreferences sharedPreferences) {
    sharedPreferences.edit().putBoolean(PartnersSecureKeys.IS_SHORTCUT_CREATED, isCreated).apply();
  }

  /**
   * @return true if the first install already appeared
   */
  public static boolean getFirstInstallAlreadyAppeared(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(PartnersSecureKeys.FIRST_INSTALL_ALREADY_APPEARED, false);
  }

  /**
   * @param alreadyAppeared set value to know if first install already appeared
   */
  public static void setFirstInstallAlreadyAppeared(boolean alreadyAppeared,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(PartnersSecureKeys.FIRST_INSTALL_ALREADY_APPEARED, alreadyAppeared)
        .apply();
  }

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
   * @return true if the daily top notification is active
   */
  public static boolean isDailyTopAppsNotificationActive(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(PartnersSecureKeys.DAILY_TOP_APP_NOTIFICATION, true);
  }

  /**
   * @param isNotificationActive set value for daily top app notification
   */
  public static void setDailyTopAppsNotificationActive(boolean isNotificationActive,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(PartnersSecureKeys.DAILY_TOP_APP_NOTIFICATION, isNotificationActive)
        .apply();
  }

  /**
   * @return true if the weekly top notification is active
   */
  public static boolean isWeeklyTopAppsNotificationActive(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(PartnersSecureKeys.WEEKLY_TOP_APP_NOTIFICATION, true);
  }

  /**
   * @param isNotificationActive set value for weekly top app notification
   */
  public static void setWeeklyTopAppsNotificationActive(boolean isNotificationActive,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(PartnersSecureKeys.WEEKLY_TOP_APP_NOTIFICATION, isNotificationActive)
        .apply();
  }
}