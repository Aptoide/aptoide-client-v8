package cm.aptoide.accountmanager;

import android.util.Log;

/**
 * Created by hsousa on 23-07-2015.
 */
public class Logger {

	public static final boolean DBG = true;//Aptoide.DEBUG_MODE;

	/**
	 * Depending on the DEBUG flag, prints the stacktrace
	 *
	 * @param e
	 */
	public static void printException(Exception e) {
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