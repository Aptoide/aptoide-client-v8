/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

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
		return new PaidAppProduct(payment.metadata.id, "paidapk", app.getIcon(), payment.amount, app.getPay().getCurrency(),
				payment.payment_services.get(0).getTaxRate(), app.getId(), app.getName(), app.getStore().getName());
	}

	public Product create(InAppBillingSkuDetailsResponse.Metadata metadata, int apiVersion, String developerPayload, String packageName, String sku) {
		return new InAppBillingProduct(metadata.getId(), "iab", metadata.getIcon(), metadata.getPrice(), metadata.getCurrency(),
				metadata.getTaxRate(), developerPayload, apiVersion, packageName, sku);
	}
}
