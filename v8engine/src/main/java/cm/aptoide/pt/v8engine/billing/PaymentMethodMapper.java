/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;

public class PaymentMethodMapper {

  public static final int PAYPAL = 1;
  public static final int BOA_COMPRA = 7;
  public static final int SANDBOX = 8;
  public static final int BOA_COMPRA_GOLD = 9;
  public static final int MOL_POINTS = 10;
  public static final int BRAINTREE_CREDIT_CARD = 11;

  public PaymentMethod map(PaymentServiceResponse paymentService) {
    switch (paymentService.getId()) {
      case PAYPAL:
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
      case MOL_POINTS:
      case BRAINTREE_CREDIT_CARD:
      case SANDBOX:
        return new PaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription());
      default:
        throw new IllegalArgumentException("Payment not supported: " + paymentService.getName());
    }
  }
}