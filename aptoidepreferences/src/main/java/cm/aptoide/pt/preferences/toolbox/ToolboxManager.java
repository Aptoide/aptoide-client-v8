package cm.aptoide.pt.preferences.toolbox;

import android.content.Context;
import android.preference.PreferenceManager;
import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 06-06-2017.
 */

public class ToolboxManager {

  private static final Context context = Application.getContext();

  public static boolean isDebug() {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(ToolboxKeys.DEBUG, false);
  }

  public static void setDebug(boolean debug) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(ToolboxKeys.DEBUG, debug)
        .apply();
  }

  public static String getForceCountry() {
    String defaultValue = "";

    return isDebug() ? PreferenceManager.getDefaultSharedPreferences(context)
        .getString(ToolboxKeys.FORCE_COUNTRY, defaultValue) : defaultValue;
  }

  public static void setForceCountry(String forcedCountry) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putString(ToolboxKeys.FORCE_COUNTRY, forcedCountry)
        .apply();
  }

  public static boolean isToolboxEnableHttpScheme() {
    boolean defaultValue = false;

    return isDebug() ? PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(ToolboxKeys.TOOLBOX_ENABLE_HTTP_SCHEME, defaultValue) : defaultValue;
  }

  public static void setToolboxEnableHttpScheme(boolean toolboxEnableHttp) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(ToolboxKeys.TOOLBOX_ENABLE_HTTP_SCHEME, toolboxEnableHttp)
        .apply();
  }

  public static boolean isToolboxEnableRetrofitLogs() {
    boolean defaultValue = false;

    return isDebug() ? PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(ToolboxKeys.TOOLBOX_RETROFIT_LOGS, defaultValue) : defaultValue;
  }

  public static void setToolboxEnableRetrofitLogs(boolean toolboxEnableRetrofitLogs) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(ToolboxKeys.TOOLBOX_RETROFIT_LOGS, toolboxEnableRetrofitLogs)
        .apply();
  }

  public static String getNotificationType() {
    String defaultValue = "";

    return isDebug() ? PreferenceManager.getDefaultSharedPreferences(context)
        .getString(ToolboxKeys.NOTIFICATION_TYPE, defaultValue) : defaultValue;
  }

  public static void setNotificationType(String notificationType) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putString(ToolboxKeys.NOTIFICATION_TYPE, notificationType)
        .apply();
  }

  public static long getPushNotificationPullingInterval() {
    long defaultValue = -1;

    return isDebug() ? PreferenceManager.getDefaultSharedPreferences(context)
        .getLong(ToolboxKeys.PUSH_NOTIFICATION_PULL_INTERVAL, defaultValue) : defaultValue;
  }

  /**
   * @param intervalTime time in ms
   */
  public static void setPushNotificationPullingInterval(long intervalTime) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putLong(ToolboxKeys.PUSH_NOTIFICATION_PULL_INTERVAL, intervalTime)
        .apply();
  }
}
