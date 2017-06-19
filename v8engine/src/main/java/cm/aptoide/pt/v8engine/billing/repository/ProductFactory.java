/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class ProductFactory {

  public Product create(PaidApp app, boolean sponsored) {
    final String icon = app.getPath()
        .getIcon() == null ? app.getPath()
        .getAlternativeIcon() : app.getPath()
        .getIcon();
    return new PaidAppProduct(app.getPayment()
        .getMetadata()
        .getProductId(), icon, app.getApp()
        .getName(), app.getApp()
        .getDescription(), app.getPath()
        .getAppId(), app.getPath()
        .getStoreName(), new Price(app.getPayment()
        .getAmount(), app.getPayment()
        .getPaymentServices()
        .get(0)
        .getCurrency(), app.getPayment()
        .getSymbol(), app.getPayment()
        .getPaymentServices()
        .get(0)
        .getTaxRate()), sponsored, app.getPath()
        .getVersionCode());
  }

  public List<Product> create(int apiVersion, String developerPayload, String packageName,
      InAppBillingSkuDetailsResponse response, int packageVersionCode) {

    final PaymentServiceResponse paymentServiceResponse =
        (response.getPaymentServices() != null && !response.getPaymentServices()
            .isEmpty()) ? response.getPaymentServices()
            .get(0) : null;

    double taxRate = 0f;
    String sign = "";

    if (paymentServiceResponse != null) {
      taxRate = paymentServiceResponse.getTaxRate();
      sign = paymentServiceResponse.getSign();
    }

    final List<Product> products = new ArrayList<>();

    final InAppBillingSkuDetailsResponse.Metadata metadata = response.getMetadata();

    int id = 0;
    String icon = "";

    if (metadata != null) {
      id = metadata.getId();
      icon = metadata.getIcon();
    }

    for (InAppBillingSkuDetailsResponse.PurchaseDataObject purchaseDataObject : response.getPublisherResponse()
        .getDetailList()) {

      products.add(new InAppProduct(id, icon, purchaseDataObject.getTitle(),
          purchaseDataObject.getDescription(), apiVersion, purchaseDataObject.getProductId(),
          packageName, developerPayload, purchaseDataObject.getType(),
          new Price(purchaseDataObject.getPriceAmount(), purchaseDataObject.getCurrency(), sign,
              taxRate), packageVersionCode));
    }

    return products;
  }
}