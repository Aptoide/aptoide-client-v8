/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Intent;

import cm.aptoide.pt.iab.InAppBillingBinder;
import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import lombok.AllArgsConstructor;

/**
 * Created by marcelobenites on 8/26/16.
 */
@AllArgsConstructor
public class PurchaseIntentFactory {

	private final String statusKey;
	private final String dataKey;
	private final String signatureKey;

	private final int statusOk;
	private final int statusError;
	private final int statusCancelled;

	public Intent create(Purchase purchase) {
		final Intent intent = new Intent();
		intent.putExtra(statusKey, statusOk);
		if (purchase.getData() != null) {
			intent.putExtra(dataKey, purchase.getData());
		}

		if (purchase.getSignature() !=  null) {
			intent.putExtra(signatureKey, purchase.getSignature());
		}

		return intent;
	}

	public Intent create(Throwable throwable) {
		// TODO treat errors according to throwable
		return new Intent().putExtra(statusKey, statusError);
	}

	public Intent createFromCancellation() {
		return new Intent().putExtra(statusKey, statusCancelled);
	}
	
}
