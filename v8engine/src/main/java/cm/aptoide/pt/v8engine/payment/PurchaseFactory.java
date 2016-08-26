/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.iab.InAppBillingBinder;
import cm.aptoide.pt.iab.InAppBillingPurchase;
import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import lombok.AllArgsConstructor;

/**
 * Created by marcelobenites on 8/25/16.
 */
@AllArgsConstructor
public class PurchaseFactory {

	private final InAppBillingSerializer serializer;

	public Purchase create(InAppBillingPurchase inAppBillingPurchase) {
		return new Purchase() {

			@Override
			public String getData() {
				return serializer.serializePurchase(inAppBillingPurchase);
			}

			@Override
			public String getSignature() {
				return inAppBillingPurchase.getSignature();
			}
		};
	}

	public Purchase create(GetApkInfoJson.Payment payment) {
		return new Purchase() {

			@Override
			public String getData() {
				return null;
			}

			@Override
			public String getSignature() {
				return null;
			}
		};
	}
}
