/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/05/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.model.v7.BaseV7Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 02-05-2016.
 */
public class ObservableUtils {

	public static <T> Observable<T> retryOnTicket(Observable<T> observable) {
		return observable.subscribeOn(Schedulers.io()).flatMap(t -> {
			if (((BaseV7Response) t).getInfo().getStatus().equals(BaseV7Response.Info.Status.QUEUED)) {
				return Observable.error(new ToRetryThrowable());
			} else {
				return Observable.just(t);
			}
		}).retryWhen(observable1 -> observable1.zipWith(Observable.range(1, 3), (n, i) -> {
			if ((n instanceof ToRetryThrowable) && i < 3) {
				return i;
			} else {
				// Don't retry
				throw new NoNetworkConnectionException(n);
			}
		}).delay(500, TimeUnit.MILLISECONDS));
	}
}
