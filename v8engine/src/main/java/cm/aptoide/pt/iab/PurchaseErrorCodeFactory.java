/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.iab;

import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;

/**
 * Created by marcelobenites on 8/29/16.
 */
public class PurchaseErrorCodeFactory extends ErrorCodeFactory {

	@Override
	public int create(Throwable throwable) {
		if (throwable instanceof RepositoryItemNotFoundException) {
			return BillingBinder.RESULT_ITEM_NOT_OWNED;
		}
		return super.create(throwable);
	}
}
