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

	public static final boolean DBG = BuildConfig.DEBUG;

	/**
	 * Prints the stacktrace
	 *
	 * @param e exception to log.
	 */
	public static void printException(Throwable e) {
		if (DBG && e != null) {
			e.printStackTrace();
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
		if (DBG) {
			Log.w(TAG, msg);
		}
	}

	public static void w(String TAG, String msg, Throwable tr) {
		if (DBG) {
			Log.w(TAG, msg, tr);
		}
	}

	public static void d(String TAG, String msg) {
		if (DBG) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (DBG) {
			Log.e(TAG, msg);
		}
	}

	public static void e(String TAG, Throwable tr) {
		if (DBG) {
			Log.e(TAG, "", tr);
		}
	}

	public static void e(String TAG, String msg, Throwable tr) {
		if (DBG) {
			Log.e(TAG, msg, tr);
		}
	}

	public static void v(String TAG, String msg) {
		if (DBG) {
			Log.v(TAG, msg);
		}
	}

	public static void v(String TAG, String msg, Throwable tr) {
		if (DBG) {
			Log.v(TAG, msg, tr);
		}
	}
}
