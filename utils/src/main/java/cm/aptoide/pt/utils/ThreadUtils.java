/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.utils;

import android.os.Looper;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 02-05-2016.
 */
public class ThreadUtils {

	public static void runOnUiThread(Runnable runnable) {
		Observable.just(null).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> runnable.run());
	}

	public static void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean isOnUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
}
