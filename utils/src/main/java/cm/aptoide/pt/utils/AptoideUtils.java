/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/05/2016.
 */

package cm.aptoide.pt.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 02-05-2016.
 */
public class AptoideUtils {

	public static void runOnUiThread(Runnable runnable) {
		Observable.just(null).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> runnable.run());
	}
}
