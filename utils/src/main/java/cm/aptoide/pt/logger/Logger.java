/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/06/2016.
 */

package cm.aptoide.pt.logger;

import android.util.Log;
import cm.aptoide.pt.utils.BuildConfig;

/**
 * Aptoide default logger.
 */
public class Logger {

  private static final boolean DBG = BuildConfig.DEBUG;

  /**
   * Prints the stacktrace
   *
   * @param e exception to log.
   */
  public static void printException(Throwable e) {
    if (e != null) {
      e.printStackTrace();
    }
  }

  public static void v(String tag, String msg) {
    if (DBG) {
      Log.v(tag, msg);
    }
  }

  public static void v(String tag, String msg, Throwable tr) {
    if (DBG) {
      Log.v(tag, msg, tr);
    }
  }

  public static void d(String tag, String msg) {
    if (DBG) {
      Log.d(tag, msg);
    }
  }

  public static void d(String tag, String msg, Throwable tr) {
    if (DBG) {
      Log.d(tag, msg, tr);
    }
  }

  public static void i(Object object, String msg) {
    i(object.getClass().getSimpleName(), msg);
  }

  public static void i(Class clz, String msg) {
    i(clz.getSimpleName(), msg);
  }

  public static void i(String TAG, String msg) {
    if (DBG) {
      Log.i(TAG, msg);
    }
  }

  public static void w(String TAG, String msg) {
    Log.w(TAG, msg);
  }

  public static void w(String TAG, String msg, Throwable tr) {
    Log.w(TAG, msg, tr);
  }

  public static void e(String TAG, String msg) {
    Log.e(TAG, msg);
  }

  public static void e(String TAG, Throwable tr) {
    Log.e(TAG, "", tr);
  }

  public static void e(String TAG, String msg, Throwable tr) {
    Log.e(TAG, msg, tr);
  }
}
