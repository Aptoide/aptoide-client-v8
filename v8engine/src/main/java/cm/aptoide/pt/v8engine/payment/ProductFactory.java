/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

  public AptoideProduct create(GetAppMeta.App app) {
    return new PaidAppProduct(app.getPay().getProductId(), app.getIcon(), app.getName(),
        app.getMedia().getDescription(), app.getId(),
        app.getStore().getName());
  }

  public AptoideProduct create(InAppBillingSkuDetailsResponse.Metadata metadata, int apiVersion,
      String developerPayload, String packageName,
      InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject) {
    return new InAppBillingProduct(metadata.getId(), metadata.getIcon(),
        purchaseDataObject.getTitle(), purchaseDataObject.getDescription(), apiVersion, purchaseDataObject.getProductId(), packageName,
        developerPayload, purchaseDataObject.getType());
  }
}