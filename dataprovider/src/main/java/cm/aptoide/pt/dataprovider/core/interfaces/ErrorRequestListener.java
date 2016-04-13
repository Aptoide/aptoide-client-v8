package cm.aptoide.pt.dataprovider.core.interfaces;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Request onError Interface.
 */
public interface ErrorRequestListener {

	void onError(HttpException e);
}
