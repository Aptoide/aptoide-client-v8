/*
 * Copyright (c) 2016.
 * Modified on 20/06/2016.
 */

package cm.aptoide.pt.logger;

import android.util.Log;
import cm.aptoide.analytics.DebugLogger;

/**
 * Aptoide default logger.
 */
public class Logger implements DebugLogger {

  private static boolean DBG;
  private static Logger instance = null;

  public synchronized static Logger getInstance() {
    if (instance == null) {
      instance = new Logger();
    }
    return instance;
  }

  public static void setDBG(boolean DBG) {
    Logger.DBG = DBG;
  }

  @Override public void v(String tag, String msg) {
    if (DBG && msg != null) {
      Log.v(tag, msg);
    }
  }

  @Override public void v(String tag, String msg, Throwable tr) {
    if (DBG && msg != null) {
      Log.v(tag, msg, tr);
    }
  }

  @Override public void d(Object object, String msg) {
    d(object.getClass()
        .getSimpleName(), msg);
  }

  @Override public void d(String tag, String msg) {
    if (DBG && msg != null) {
      Log.d(tag, msg);
    }
  }

  @Override public void d(String tag, String msg, Throwable tr) {
    if (DBG && msg != null) {
      Log.d(tag, msg, tr);
    }
  }

  @Override public void i(Object object, String msg) {
    i(object.getClass()
        .getSimpleName(), msg);
  }

  @Override public void i(String tag, String msg) {
    if (DBG && msg != null) {
      Log.i(tag, msg);
    }
  }

  @Override public void i(Class clz, String msg) {
    i(clz.getSimpleName(), msg);
  }

  @Override public void w(String TAG, String msg) {
    if (msg != null) {
      Log.w(TAG, msg);
    }
  }

  @Override public void w(String TAG, String msg, Throwable tr) {
    if (msg != null) {
      Log.w(TAG, msg, tr);
    }
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  @Override public void e(Object object, String msg) {
    e(object.getClass()
        .getName(), msg);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  @Override public void e(String TAG, String msg) {
    if (msg != null) {
      Log.e(TAG, msg);
    }
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  @Override public void e(Object object, Throwable tr) {
    e(object.getClass()
        .getName(), tr);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  @Override public void e(String TAG, Throwable tr) {
    Log.e(TAG, "", tr);
  }

  /**
   * Instead of calling this method, consider using CrashReport.getInstance().log(Exception)
   */
  @Override public void e(String TAG, String msg, Throwable tr) {
    if (msg != null) {
      Log.e(TAG, msg, tr);
    }
  }
}
