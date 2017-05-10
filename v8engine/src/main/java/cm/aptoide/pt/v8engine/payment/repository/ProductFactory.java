/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

  public Product create(PaidApp app, boolean sponsored) {
    final String icon = app.getPath().getIcon() == null ? app.getPath().getAlternativeIcon()
        : app.getPath().getIcon();
    return new PaidAppProduct(app.getPayment().getMetadata().getProductId(), icon,
        app.getApp().getName(), app.getApp().getDescription(), app.getPath().getAppId(),
        app.getPath().getStoreName(), new Price(app.getPayment().getAmount(),
        app.getPayment().getPaymentServices().get(0).getCurrency(), app.getPayment().getSymbol(),
        app.getPayment().getPaymentServices().get(0).getTaxRate()), sponsored);
  }

  public Product create(int apiVersion, String developerPayload, String packageName,
      InAppBillingSkuDetailsResponse response) {
    final InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject =
        response.getPublisherResponse().getDetailList().get(0);
    PaymentServiceResponse paymentServiceResponse = response.getPaymentServices().get(0);
    return new InAppBillingProduct(response.getMetadata().getId(), response.getMetadata().getIcon(),
        purchaseDataObject.getTitle(), purchaseDataObject.getDescription(), apiVersion,
        purchaseDataObject.getProductId(), packageName, developerPayload,
        purchaseDataObject.getType(),
        new Price(purchaseDataObject.getPriceAmount(), purchaseDataObject.getCurrency(),
            paymentServiceResponse.getSign(), paymentServiceResponse.getTaxRate()));
  }
}