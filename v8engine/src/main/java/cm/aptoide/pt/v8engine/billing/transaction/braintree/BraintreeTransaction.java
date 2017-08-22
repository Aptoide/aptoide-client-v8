package cm.aptoide.pt.v8engine.billing.transaction.braintree;

import cm.aptoide.pt.v8engine.billing.transaction.Transaction;

public class BraintreeTransaction extends Transaction {

  private final String token;

  public BraintreeTransaction(String productId, String payerId, Status status, int paymentMethodId,
      String token, String payload, String sellerId) {
    super(productId, payerId, status, paymentMethodId, payload, sellerId);
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
