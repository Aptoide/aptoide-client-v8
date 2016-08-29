/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Intent;

import java.io.IOException;

import cm.aptoide.pt.iab.BillingBinder;
import cm.aptoide.pt.iab.ErrorCodeFactory;
import lombok.AllArgsConstructor;

/**
 * Created by marcelobenites on 8/26/16.
 */
@AllArgsConstructor
public class PurchaseIntentFactory {

	private final ErrorCodeFactory codeFactory;

	public Intent create(Purchase purchase) throws IOException {
		final Intent intent = new Intent();
		intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_OK);
		if (purchase.getData() != null) {
			intent.putExtra(BillingBinder.INAPP_PURCHASE_DATA, purchase.getData());
		}

		if (purchase.getSignature() !=  null) {
			intent.putExtra(BillingBinder.INAPP_DATA_SIGNATURE, purchase.getSignature());
		}

		return intent;
	}

	public Intent create(Throwable throwable) {
		return new Intent().putExtra(BillingBinder.RESPONSE_CODE, codeFactory.create(throwable));
	}

	public Intent createFromCancellation() {
		return new Intent().putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_USER_CANCELED);
	}
	
}
