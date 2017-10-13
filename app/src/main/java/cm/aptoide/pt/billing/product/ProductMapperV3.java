/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.IdResolver;
import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.dataprovider.ws.v3.V3;

public class ProductMapperV3 {

  private final IdResolver idResolver;

  public ProductMapperV3(IdResolver idResolver) {
    this.idResolver = idResolver;
  }

  public Product map(PaidApp response) {

    if (response.isOk() && response.isPaid()) {

      final String icon = response.getPath()
          .getIcon() == null ? response.getPath()
          .getAlternativeIcon() : response.getPath()
          .getIcon();

      final PaidApp.Payment payment = response.getPayment();
      final PaymentServiceResponse paymentService =
          (payment.getPaymentServices() != null && !payment.getPaymentServices()
              .isEmpty()) ? payment.getPaymentServices()
              .get(0) : null;
      final String currency = (paymentService != null) ? paymentService.getCurrency() : "";

      final String title = response.getApp()
          .getName();
      final String description = response.getApp()
          .getDescription();
      final Price price = new Price(payment.getAmount(), currency, payment.getSymbol());
      final int packageVersionCode = response.getPath()
          .getVersionCode();

      return new SimpleProduct(idResolver.generateProductId(response.getPath()
          .getAppId()), icon, title, description, price, packageVersionCode);
    }

    throw new IllegalArgumentException(V3.getErrorMessage(response));
  }
}
