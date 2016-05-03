/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 03/05/2016.
 */

package cm.aptoide.pt.logger;

import android.util.Log;

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
}