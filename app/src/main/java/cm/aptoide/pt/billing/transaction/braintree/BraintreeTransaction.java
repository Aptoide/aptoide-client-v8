package cm.aptoide.pt.billing.transaction.braintree;

import cm.aptoide.pt.billing.transaction.Transaction;

public class BraintreeTransaction extends Transaction {

  private final String token;

  public BraintreeTransaction(String productId, String customerId, Status status, int paymentMethodId,
      String token, String payload, String merchantName) {
    super(productId, customerId, status, paymentMethodId, payload, merchantName);
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
