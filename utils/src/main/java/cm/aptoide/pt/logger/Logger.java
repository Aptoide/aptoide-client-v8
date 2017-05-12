/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/06/2016.
 */

package cm.aptoide.pt.logger;

import android.util.Log;
import lombok.Setter;

/**
 * Aptoide default logger.
 */
public class Logger {

  @Setter private static boolean DBG;

  public static void v(String tag, String msg) {
    if (DBG && msg != null) {
      Log.v(tag, msg);
    }
  }

  public static void v(String tag, String msg, Throwable tr) {
    if (DBG && msg != null) {
      Log.v(tag, msg, tr);
    }
  }

  public static void d(Object object, String msg) {
    d(object.getClass()
        .getSimpleName(), msg);
  }

  public static void d(String tag, String msg) {
    if (DBG && msg != null) {
      Log.d(tag, msg);
    }
  }

  public static void d(String tag, String msg, Throwable tr) {
    if (DBG && msg != null) {
      Log.d(tag, msg, tr);
    }
  }

  public static void i(Object object, String msg) {
    i(object.getClass()
        .getSimpleName(), msg);
  }

  public static void i(String tag, String msg) {
    if (DBG && msg != null) {
      Log.i(tag, msg);
    }
  }

  public static void i(Class clz, String msg) {
    i(clz.getSimpleName(), msg);
  }

  public static void w(String TAG, String msg) {
    if (msg != null) {
      Log.w(TAG, msg);
    }
  }

  public static void w(String TAG, String msg, Throwable tr) {
    if (msg != null) {
      Log.w(TAG, msg, tr);
    }
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  public static void e(Object object, String msg) {
    e(object.getClass()
        .getName(), msg);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  public static void e(String TAG, String msg) {
    if (msg != null) {
      Log.e(TAG, msg);
    }
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  public static void e(Object object, Throwable tr) {
    e(object.getClass()
        .getName(), tr);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  public static void e(String TAG, Throwable tr) {
    Log.e(TAG, "", tr);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  public static void e(String TAG, String msg, Throwable tr) {
    if (msg != null) {
      Log.e(TAG, msg, tr);
    }
  }
}
