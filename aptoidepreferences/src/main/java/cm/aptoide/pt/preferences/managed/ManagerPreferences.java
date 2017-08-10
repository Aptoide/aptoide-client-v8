/*
 * Copyright (c) 2016.
 * Modified on 29/08/2016.
 */

package cm.aptoide.pt.preferences.managed;

import android.content.SharedPreferences;

/**
 * Created by neuro on 21-04-2016.
 */
public class ManagerPreferences {

  public static boolean getHWSpecsFilter(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.HWSPECS_FILTER, true);
  }

  public static void setHWSpecsFilter(boolean flag, SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.HWSPECS_FILTER, flag)
        .apply();
  }

  /**
   * @param sharedPreferences
   *
   * @return true when updates should hide alpha and beta versions.
   */
  public static boolean getUpdatesFilterAlphaBetaKey(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.UPDATES_FILTER_ALPHA_BETA_KEY, false);
  }

  /**
   * @param sharedPreferences
   *
   * @return true when updates should include system apps
   */
  public static boolean getUpdatesSystemAppsKey(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.UPDATES_SYSTEM_APPS_KEY, false);
  }

  public static int getLastPushNotificationId(SharedPreferences sharedPreferences) {
    return sharedPreferences.getInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, 0);
  }

  public static void setLastPushNotificationId(int notificationId,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, notificationId)
        .apply();
  }

  public static boolean getGeneralDownloadsWifi(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.GENERAL_DOWNLOADS_WIFI, true);
  }

  public static boolean scheduledDownloadsEnabled(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.SCHEDULE_DOWNLOAD_SETTING, true);
  }

  public static boolean getGeneralDownloadsMobile(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.GENERAL_DOWNLOADS_MOBILE, true);
  }

  public static boolean getAnimationsEnabledStatus(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.ANIMATIONS_ENABLED, true);
  }

  public static boolean isCheckAutoUpdateEnable(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.CHECK_AUTO_UPDATE, true);
  }

  public static boolean isAutoUpdateEnable(SharedPreferences defaultSharedPreferences) {
    //the default value should match with auto_update from settings.xml
    return defaultSharedPreferences.getBoolean(ManagedKeys.AUTO_UPDATE_ENABLE, false);
  }

  public static void setAutoUpdateEnable(boolean state, SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.AUTO_UPDATE_ENABLE, state)
        .apply();
  }

  public static boolean isAllwaysUpdate(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.PREF_ALWAYS_UPDATE, false);
  }

  public static int getLastUpdates(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getInt(ManagedKeys.LAST_UPDATES_KEY, 0);
  }

  public static void setLastUpdates(int lastUpdates, SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putInt(ManagedKeys.LAST_UPDATES_KEY, lastUpdates)
        .apply();
  }

  public static long getCacheLimit(SharedPreferences defaultSharedPreferences) {
    String chacheLimit = defaultSharedPreferences.getString(ManagedKeys.MAX_FILE_CACHE, "300");
    try {
      return Long.parseLong(chacheLimit);
    } catch (Exception e) {
      return 200;
    }
  }

  public static boolean getAndResetForceServerRefresh(SharedPreferences sharedPreferences) {
    boolean state = sharedPreferences.getBoolean(ManagedKeys.FORCE_SERVER_REFRESH_FLAG, false);
    if (state) {
      setForceServerRefreshFlag(false, sharedPreferences);
    }
    return state;
  }

  public static void setForceServerRefreshFlag(boolean state, SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.FORCE_SERVER_REFRESH_FLAG, state)
        .apply();
  }

  public static boolean needsSqliteDbMigration(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.PREF_NEEDS_SQLITE_DB_MIGRATION, true);
  }

  public static void setNeedsSqliteDbMigration(boolean migrationNeeded,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.PREF_NEEDS_SQLITE_DB_MIGRATION, migrationNeeded)
        .apply();
  }

  public static boolean isUpdateNotificationEnable(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.PREF_SHOW_UPDATE_NOTIFICATION, true);
  }

  public static boolean allowRootInstallation(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.ALLOW_ROOT_INSTALATION, false);
  }

  public static void setAllowRootInstallation(boolean allowRootInstallation,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.ALLOW_ROOT_INSTALATION, allowRootInstallation)
        .apply();
  }

  public static boolean getUserPrivacyConfirmation(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.PRIVACY_CONFIRMATION, true);
  }

  public static boolean isShowPreviewDialog(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.DONT_SHOW_ME_AGAIN, true);
  }

  public static void setShowPreviewDialog(boolean showPreviewDialog,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.DONT_SHOW_ME_AGAIN, showPreviewDialog)
        .apply();
  }

  public static String getPreviewDialogPrefVersionCleaned(
      SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getString(ManagedKeys.DONT_SHOW_CLEANED_VERSION, "");
  }

  public static void setPreviewDialogPrefVersionCleaned(String previewDialogPrefCleaned,
      SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putString(ManagedKeys.DONT_SHOW_CLEANED_VERSION, previewDialogPrefCleaned)
        .apply();
  }

  public static boolean isFirstRunV7(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.FIRST_RUN_V7, true);
  }

  public static boolean getAddressBookSyncState(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.ADDRESS_BOOK_SYNC, false);
  }

  public static void setAddressBookAsSynced(SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.ADDRESS_BOOK_SYNC, true)
        .apply();
  }

  public static boolean getTwitterSyncState(SharedPreferences defaultSharedPreferences) {
    return defaultSharedPreferences.getBoolean(ManagedKeys.TWITTER_SYNC, false);
  }

  public static void setTwitterAsSynced(SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.TWITTER_SYNC, true)
        .apply();
  }

  public static boolean getFacebookSyncState(SharedPreferences sharedPreferences) {
    return sharedPreferences.getBoolean(ManagedKeys.FACEBOOK_SYNC, false);
  }

  public static void setFacebookAsSynced(SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.FACEBOOK_SYNC, true)
        .apply();
  }

  public static void setAddressBookSyncValues(Boolean value, SharedPreferences sharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.ADDRESS_BOOK_SYNC, value)
        .apply();
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.TWITTER_SYNC, value)
        .apply();
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.FACEBOOK_SYNC, value)
        .apply();
  }
}
