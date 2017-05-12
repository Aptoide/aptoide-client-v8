/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

  public Product create(GetAppMeta.App app) {
    return new PaidAppProduct(app.getPay()
        .getProductId(), app.getIcon(), app.getName(), app.getMedia()
        .getDescription(), app.getId(), app.getStore()
        .getName(), new Price(app.getPay()
        .getPrice(), app.getPay()
        .getCurrency(), app.getPay()
        .getSymbol(), app.getPay()
        .getTaxRate()));
  }

  public Product create(int apiVersion, String developerPayload, String packageName,
      InAppBillingSkuDetailsResponse response) {
    final InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject =
        response.getPublisherResponse()
            .getDetailList()
            .get(0);
    PaymentServiceResponse paymentServiceResponse = response.getPaymentServices()
        .get(0);
    return new InAppBillingProduct(response.getMetadata()
        .getId(), response.getMetadata()
        .getIcon(), purchaseDataObject.getTitle(), purchaseDataObject.getDescription(), apiVersion,
        purchaseDataObject.getProductId(), packageName, developerPayload,
        purchaseDataObject.getType(),
        new Price(purchaseDataObject.getPriceAmount(), purchaseDataObject.getCurrency(),
            paymentServiceResponse.getSign(), paymentServiceResponse.getTaxRate()));
  }
}