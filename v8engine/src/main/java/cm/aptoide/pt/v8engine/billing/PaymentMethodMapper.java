/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentMethodMapper {

  public static final int PAYPAL = 1;
  public static final int BOA_COMPRA = 7;
  public static final int SANDBOX = 8;
  public static final int BOA_COMPRA_GOLD = 9;
  public static final int MOL_POINTS = 10;
  public static final int BRAINTREE_CREDIT_CARD = 11;

  public List<PaymentMethod> map(List<PaymentServiceResponse> response) {

    if (response == null || response.isEmpty()) {
      return Collections.emptyList();
    }

    final List<PaymentMethod> paymentMethods = new ArrayList<>(response.size());
    for (PaymentServiceResponse service : response) {
      try {
        paymentMethods.add(map(service));
      } catch (IllegalArgumentException ignored) {
      }
    }
    return paymentMethods;
  }

  private PaymentMethod map(PaymentServiceResponse response) {
    switch (response.getId()) {
      case PAYPAL:
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
      case MOL_POINTS:
      case BRAINTREE_CREDIT_CARD:
      case SANDBOX:
        return new PaymentMethod(response.getId(), response.getName(), response.getDescription());
      default:
        throw new IllegalArgumentException("Payment not supported: " + response.getName());
    }
  }
}