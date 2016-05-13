/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/05/2016.
 */

package cm.aptoide.pt.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sithengineer on 06/05/16.
 */
public final class ObservableUtils {

	/**
	 * code from <a href="http://blog.danlew.net/2015/03/02/dont-break-the-chain/">http://blog.danlew.net/2015/03/02/dont-break-the-chain/</a>
	 *
	 * @param <T> Observable of T
	 * @return original Observable subscribed in an io thread and observed in the main thread
	 */
	public static <T> Observable.Transformer<T, T> applySchedulers() {
		return observable ->
				observable
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread());
	}

	// consider moving the retry code from dataprovider module to here

}
