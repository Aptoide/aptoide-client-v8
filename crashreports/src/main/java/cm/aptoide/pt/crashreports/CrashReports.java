package cm.aptoide.pt.crashreports;

import cm.aptoide.pt.logger.Logger;

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashReports {

  private final static String TAG = CrashReports.class.getSimpleName();   //TAG for the logger

  private static boolean initialized = false;
  private static CrashLogger crashLogger;

  /**
   * setup crash reports
   *
   * @param crashLogger crashLogger to be used by {@link CrashReports}.
   */
  public static void setup(CrashLogger crashLogger) {
    CrashReports.crashLogger = crashLogger;
    initialized = true;
  }

  /**
   * logs exception in crashes
   *
   * @param throwable exception you want to send
   */
  public static void logException(Throwable throwable) {
    if (!initialized) {
      Logger.w(TAG, "CrashReports not initialized.");
      return;
    }

    crashLogger.logException(throwable);
  }

  /**
   * logs string in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  public static void logString(String key, String value) {
    if (!initialized) {
      Logger.w(TAG, "CrashReports not initialized.");
      return;
    }

    crashLogger.logString(key, value);
  }
}