/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.database.realm.*;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

	public Product create(GetAppMeta.App app, GetApkInfoJson.Payment payment) {
		return new PaidAppProduct(payment.metadata.id, app.getIcon(), app.getName(), app.getMedia().getDescription(), app.getPay().getPrice(), app.getId(),
				app.getStore().getName());
	}

	public Product create(InAppBillingSkuDetailsResponse.Metadata metadata, int apiVersion, String developerPayload, String packageName,
	                      InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject) {
		return new InAppBillingProduct(metadata.getId(), metadata.getIcon(), purchaseDataObject.getTitle(), purchaseDataObject.getDescription(),
				purchaseDataObject.getPrice(), apiVersion, purchaseDataObject.getSku(), packageName, developerPayload, purchaseDataObject.getType());
	}

	public Product create(cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
		final Product product;

		if (paymentConfirmation.getAppId() != 0 && paymentConfirmation.getStoreName() != null) {
			product = new InAppBillingProduct(paymentConfirmation.getProductId(), paymentConfirmation.getIcon(), paymentConfirmation.getTitle(),
					paymentConfirmation.getDescription(), paymentConfirmation.getPriceDescription(), paymentConfirmation.getApiVersion(), paymentConfirmation
					.getSku(), paymentConfirmation.getPackageName(), paymentConfirmation.getDeveloperPayload(), paymentConfirmation.getType());
		} else {
			product = new PaidAppProduct(paymentConfirmation.getProductId(), paymentConfirmation.getIcon(), paymentConfirmation.getTitle(),
					paymentConfirmation.getDescription(), paymentConfirmation.getPriceDescription(), paymentConfirmation.getAppId(), paymentConfirmation
					.getStoreName());
		}
		return product;
	}
}
