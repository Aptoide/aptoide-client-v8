/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.dataprovider.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;
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

    final PaidApp.Payment payment = app.getPayment();
    final PaidApp.Metadata metadata = payment.getMetadata();
    final int productId = (metadata != null) ? metadata.getProductId() : 0;
    final PaymentServiceResponse paymentService =
        getPaymentServiceResponse(payment.getPaymentServices());
    final String currency = (paymentService != null) ? paymentService.getCurrency() : "";
    final double taxRate = (paymentService != null) ? paymentService.getTaxRate() : 0.0;

    return new PaidAppProduct(productId, icon, app.getApp()
        .getName(), app.getApp()
        .getDescription(), app.getPath()
        .getAppId(), app.getPath()
        .getStoreName(), new Price(payment.getAmount(), currency, payment.getSymbol(), taxRate),
        sponsored, app.getPath()
        .getVersionCode());
  }

  public List<Product> create(int apiVersion, String developerPayload, String packageName,
      InAppBillingSkuDetailsResponse response, int packageVersionCode, String applicationName) {

    final PaymentServiceResponse paymentServiceResponse =
        getPaymentServiceResponse(response.getPaymentServices());

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
              taxRate), packageVersionCode, applicationName));
    }

    return products;
  }

  private PaymentServiceResponse getPaymentServiceResponse(
      List<PaymentServiceResponse> paymentServices) {
    return (paymentServices != null && !paymentServices.isEmpty()) ? paymentServices.get(0) : null;
  }
}