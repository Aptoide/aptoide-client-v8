/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;

/**
 * Created by neuro on 10-05-2016.
 */
public class ErrorUtils {

	public static boolean isNoNetworkConnection(Throwable throwable) {
		return throwable instanceof NoNetworkConnectionException || (throwable.getCause() != null
				&& throwable
				.getCause() instanceof SocketTimeoutException || throwable instanceof
				UnknownHostException || throwable instanceof ConnectException);
	}
}
