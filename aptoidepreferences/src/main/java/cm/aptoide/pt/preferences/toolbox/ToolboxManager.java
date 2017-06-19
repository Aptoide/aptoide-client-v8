package cm.aptoide.pt.preferences.toolbox;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 06-06-2017.
 */

public class ToolboxManager {

  public static boolean isDebug(SharedPreferences sharedPreferences) {
    return sharedPreferences
        .getBoolean(ToolboxKeys.DEBUG, false);
  }

  public static void setDebug(boolean debug, SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putBoolean(ToolboxKeys.DEBUG, debug)
        .apply();
  }

  public static String getForceCountry(SharedPreferences sharedPreferences) {
    String defaultValue = "";

    return isDebug(sharedPreferences) ? sharedPreferences
        .getString(ToolboxKeys.FORCE_COUNTRY, defaultValue) : defaultValue;
  }

  public static void setForceCountry(String forcedCountry, SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putString(ToolboxKeys.FORCE_COUNTRY, forcedCountry)
        .apply();
  }

  public static boolean isToolboxEnableHttpScheme(SharedPreferences sharedPreferences) {
    boolean defaultValue = false;

    return isDebug(sharedPreferences) ? sharedPreferences
        .getBoolean(ToolboxKeys.TOOLBOX_ENABLE_HTTP_SCHEME, defaultValue) : defaultValue;
  }

  public static void setToolboxEnableHttpScheme(boolean toolboxEnableHttp,
      SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putBoolean(ToolboxKeys.TOOLBOX_ENABLE_HTTP_SCHEME, toolboxEnableHttp)
        .apply();
  }

  public static boolean isToolboxEnableRetrofitLogs(SharedPreferences sharedPreferences) {
    boolean defaultValue = false;

    return isDebug(sharedPreferences) ? sharedPreferences
        .getBoolean(ToolboxKeys.TOOLBOX_RETROFIT_LOGS, defaultValue) : defaultValue;
  }

  public static void setToolboxEnableRetrofitLogs(boolean toolboxEnableRetrofitLogs,
      SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putBoolean(ToolboxKeys.TOOLBOX_RETROFIT_LOGS, toolboxEnableRetrofitLogs)
        .apply();
  }

  public static String getNotificationType(SharedPreferences sharedPreferences) {
    String defaultValue = "";

    return isDebug(sharedPreferences) ? sharedPreferences
        .getString(ToolboxKeys.NOTIFICATION_TYPE, defaultValue) : defaultValue;
  }

  public static void setNotificationType(String notificationType,
      SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putString(ToolboxKeys.NOTIFICATION_TYPE, notificationType)
        .apply();
  }

  public static long getPushNotificationPullingInterval(SharedPreferences sharedPreferences) {
    long defaultValue = -1;

    return isDebug(sharedPreferences) ? sharedPreferences
        .getLong(ToolboxKeys.PUSH_NOTIFICATION_PULL_INTERVAL, defaultValue) : defaultValue;
  }

  /**
   * @param intervalTime time in ms
   * @param sharedPreferences
   */
  public static void setPushNotificationPullingInterval(long intervalTime,
      SharedPreferences sharedPreferences) {
    sharedPreferences
        .edit()
        .putLong(ToolboxKeys.PUSH_NOTIFICATION_PULL_INTERVAL, intervalTime)
        .apply();
  }
}
