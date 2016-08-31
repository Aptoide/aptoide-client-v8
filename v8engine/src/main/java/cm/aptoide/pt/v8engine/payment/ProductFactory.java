/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

	public AptoideProduct create(GetAppMeta.App app, GetApkInfoJson.Payment payment) {
		return new PaidAppProduct(payment.getMetadata().getId(), app.getIcon(), app.getName(), app.getMedia().getDescription(), app.getPayment().getPrice(),
				app.getId(), app.getStore().getName());
	}

	public AptoideProduct create(InAppBillingSkuDetailsResponse.Metadata metadata, int apiVersion, String developerPayload, String packageName,
	                      InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject) {
		return new InAppBillingProduct(metadata.getId(), metadata.getIcon(), purchaseDataObject.getTitle(), purchaseDataObject.getDescription(),
				purchaseDataObject.getPrice(), apiVersion, purchaseDataObject.getProductId(), packageName, developerPayload, purchaseDataObject.getType());
	}

	public AptoideProduct create(cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
		final AptoideProduct product;

		if (paymentConfirmation.getAppId() != 0 && paymentConfirmation.getStoreName() != null) {
			product = new PaidAppProduct(paymentConfirmation.getProductId(), paymentConfirmation.getIcon(), paymentConfirmation.getTitle(),
					paymentConfirmation.getDescription(), paymentConfirmation.getPriceDescription(), paymentConfirmation.getAppId(), paymentConfirmation
					.getStoreName());
		} else {
			product = new InAppBillingProduct(paymentConfirmation.getProductId(), paymentConfirmation.getIcon(), paymentConfirmation.getTitle(),
					paymentConfirmation.getDescription(), paymentConfirmation.getPriceDescription(), paymentConfirmation.getApiVersion(), paymentConfirmation
					.getSku(), paymentConfirmation.getPackageName(), paymentConfirmation.getDeveloperPayload(), paymentConfirmation.getType());
		}
		return product;
	}
}
