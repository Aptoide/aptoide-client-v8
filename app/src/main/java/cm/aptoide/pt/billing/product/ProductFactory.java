/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import java.util.ArrayList;
import java.util.List;

public class ProductFactory {

  public Product create(PaidApp app) {

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

    return new PaidAppProduct(productId, app.getPath()
        .getStoreName(), icon, app.getApp()
        .getName(), app.getApp()
        .getDescription(), app.getPath()
        .getAppId(), new Price(payment.getAmount(), currency, payment.getSymbol()), app.getPath()
        .getVersionCode());
  }

  public List<Product> create(String packageName,
      List<GetProductsRequest.ResponseBody.Product> responseList, int packageVersionCode,
      String applicationName) {

    final List<Product> products = new ArrayList<>(responseList.size());

    for (GetProductsRequest.ResponseBody.Product response : responseList) {

      products.add(create(packageName, packageVersionCode, applicationName, response));
    }

    return products;
  }

  public Product create(String packageName, int packageVersionCode, String applicationName,
      GetProductsRequest.ResponseBody.Product response) {
    return new InAppProduct(response.getId(), response.getSku(), response.getIcon(),
        response.getTitle(), response.getDescription(), packageName, new Price(response.getPrice()
        .getAmount(), response.getPrice()
        .getCurrency(), response.getPrice()
        .getSign()), packageVersionCode, applicationName);
  }

  private PaymentServiceResponse getPaymentServiceResponse(
      List<PaymentServiceResponse> paymentServices) {
    return (paymentServices != null && !paymentServices.isEmpty()) ? paymentServices.get(0) : null;
  }
}
