/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
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

  public List<PaymentMethod> map(List<GetServicesRequest.ResponseBody.Service> responseList) {

    final List<PaymentMethod> paymentMethods = new ArrayList<>(responseList.size());
    for (GetServicesRequest.ResponseBody.Service service : responseList) {
      try {
        paymentMethods.add(map(service));
      } catch (IllegalArgumentException ignored) {
      }
    }
    return paymentMethods;
  }

  private PaymentMethod map(GetServicesRequest.ResponseBody.Service response) {
    switch (response.getId()) {
      case BRAINTREE_CREDIT_CARD:
        return new PaymentMethod(response.getId(), response.getName(), response.getLabel(),
            R.drawable.credit_cards);
      case PAYPAL:
        return new PaymentMethod(response.getId(), response.getName(), response.getLabel(),
            R.drawable.paypal);
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
      case MOL_POINTS:
      case SANDBOX:
        return new PaymentMethod(response.getId(), response.getName(), response.getLabel());
      default:
        throw new IllegalArgumentException("Payment not supported: " + response.getName());
    }
  }
}