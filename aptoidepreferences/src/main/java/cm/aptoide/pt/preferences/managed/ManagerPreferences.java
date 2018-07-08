/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.preferences.managed;

import android.preference.PreferenceManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by neuro on 21-04-2016.
 */
public class ManagerPreferences {

  public static boolean getHWSpecsFilter() {
    return Preferences.get().getBoolean(ManagedKeys.HWSPECS_FILTER, true);
  }

  public static void setHWSpecsFilter(boolean flag) {
    Preferences.get().edit().putBoolean(ManagedKeys.HWSPECS_FILTER, flag).apply();
  }

  /**
   * @return true when updates should hide alpha and beta versions.
   */
  public static boolean getUpdatesFilterAlphaBetaKey() {
    return Preferences.get().getBoolean(ManagedKeys.UPDATES_FILTER_ALPHA_BETA_KEY, false);
  }

  /**
   * @return true when updates should include system apps
   */
  public static boolean getUpdatesSystemAppsKey() {
    return Preferences.get().getBoolean(ManagedKeys.UPDATES_SYSTEM_APPS_KEY, false);
  }

  public static int getLastPushNotificationId() {
    return Preferences.get().getInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, 0);
  }

  public static void setLastPushNotificationId(int notificationId) {
    Preferences.get().edit().putInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, notificationId).apply();
  }

  public static boolean getGeneralDownloadsWifi() {
    return Preferences.get().getBoolean(ManagedKeys.GENERAL_DOWNLOADS_WIFI, true);
  }

  public static boolean scheduledDownloadsEnabled() {
    return Preferences.get().getBoolean(ManagedKeys.SCHEDULE_DOWNLOAD_SETTING, true);
  }

  public static boolean getGeneralDownloadsMobile() {
    return Preferences.get().getBoolean(ManagedKeys.GENERAL_DOWNLOADS_MOBILE, true);
  }

  public static void setGeneralDownloadsMobile(boolean state) {
    Preferences.get().edit().putBoolean(ManagedKeys.GENERAL_DOWNLOADS_MOBILE, state).apply();
  }

  public static boolean getAnimationsEnabledStatus() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.ANIMATIONS_ENABLED, true);
  }

  public static boolean isAutoUpdateEnable() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.CHECK_AUTO_UPDATE, true);
  }

  public static boolean isAllwaysUpdate() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.PREF_ALWAYS_UPDATE, false);
  }

  public static int getLastUpdates() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getInt(ManagedKeys.LAST_UPDATES_KEY, 0);
  }

  public static void setLastUpdates(int lastUpdates) {
    Preferences.get().edit().putInt(ManagedKeys.LAST_UPDATES_KEY, lastUpdates).apply();
  }

  public static long getCacheLimit() {
    String chacheLimit = PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getString(ManagedKeys.MAX_FILE_CACHE, "300");
    try {
      return Long.parseLong(chacheLimit);
    } catch (Exception e) {
      return 200;
    }
  }

  public static boolean getAndResetForceServerRefresh() {
    boolean state = PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.FORCE_SERVER_REFRESH_FLAG, false);
    if (state) {
      setForceServerRefreshFlag(false);
    }
    return state;
  }

  public static void setForceServerRefreshFlag(boolean state) {
    Preferences.get().edit().putBoolean(ManagedKeys.FORCE_SERVER_REFRESH_FLAG, state).apply();
  }

  public static boolean needsSqliteDbMigration() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.PREF_NEEDS_SQLITE_DB_MIGRATION, true);
  }

  public static void setNeedsSqliteDbMigration(boolean migrationNeeded) {
    Preferences.get()
        .edit()
        .putBoolean(ManagedKeys.PREF_NEEDS_SQLITE_DB_MIGRATION, migrationNeeded)
        .apply();
  }

  public static boolean isUpdateNotificationEnable() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.PREF_SHOW_UPDATE_NOTIFICATION, true);
  }

  public static boolean allowRootInstallation() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.ALLOW_ROOT_INSTALATION, false);
  }

  public static String getForceCountry() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getString(ManagedKeys.FORCE_COUNTRY, "");
  }

  public static void setForceCountry(String forcedCountry) {
    Preferences.get().edit().putString(ManagedKeys.FORCE_COUNTRY, forcedCountry).apply();
  }

  public static boolean isDebug() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.DEBUG, false);
  }

  public static void setDebug(boolean debug) {
    Preferences.get().edit().putBoolean(ManagedKeys.DEBUG, debug).apply();
  }

  public static String getNotificationType() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getString(ManagedKeys.NOTIFICATION_TYPE, "");
  }

  public static void setNotificationType(String notificationType) {
    Preferences.get().edit().putString(ManagedKeys.NOTIFICATION_TYPE, notificationType).apply();
  }

  public static void setAllowRootInstallation(boolean allowRootInstallation) {
    Preferences.get()
        .edit()
        .putBoolean(ManagedKeys.ALLOW_ROOT_INSTALATION, allowRootInstallation)
        .apply();
  }

  public static boolean getUserPrivacyConfirmation() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.PRIVACY_CONFIRMATION, true);
  }

  public static boolean getShowPreview() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.SHOW_SHARE_PREVIEW, true);
  }

  public static void setShowPreview(boolean previewShower) {
    Preferences.get().edit().putBoolean(ManagedKeys.SHOW_SHARE_PREVIEW, previewShower).apply();
  }

  public static String getUserAccess() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getString(ManagedKeys.ACCESS, "UNLISTED");
  }

  public static void setUserAccess(String access) {
    Preferences.get().edit().putString(ManagedKeys.ACCESS, access).apply();
  }

  public static Boolean getUserAccessConfirmed() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.ACCESS_CONFIRMED, false);
  }

  public static void setUserAccessConfirmed(Boolean accessConfirmed) {
    Preferences.get().edit().putBoolean(ManagedKeys.ACCESS_CONFIRMED, accessConfirmed).apply();
  }

  public static boolean isFirstRunV7() {
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext())
        .getBoolean(ManagedKeys.FIRST_RUN_V7, true);
  }


  public static String getLanguage(){
    return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getString(ManagedKeys.LANGUAGE, AptoideUtils.SystemU.getCountryCode());
  }

  public static void setLanguage(String language){
    Preferences.get().edit().putString(ManagedKeys.LANGUAGE, language).apply();
  }
}
