/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient.interfaces;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Request onError Interface.
 */
public interface ErrorRequestListener {

	void onError(HttpException e);
}
