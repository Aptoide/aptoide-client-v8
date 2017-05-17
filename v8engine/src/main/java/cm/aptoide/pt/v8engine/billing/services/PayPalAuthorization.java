package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Authorization;

public class PayPalAuthorization extends Authorization {

  public PayPalAuthorization(int paymentId, Status status, String payerId) {
    super(paymentId, payerId, status);
  }

}
