package cm.aptoide.pt.v8engine.billing.transaction.mol;

import cm.aptoide.pt.v8engine.billing.transaction.Transaction;

public class MolTransaction extends Transaction {

  private final String confirmationUrl;
  private final String successUrl;

  public MolTransaction(String productId, String payerId, Status status, int paymentMethodId,
      String confirmationUrl, String successUrl, String payload, String sellerId) {
    super(productId, payerId, status, paymentMethodId, payload, sellerId);
    this.confirmationUrl = confirmationUrl;
    this.successUrl = successUrl;
  }

  public String getConfirmationUrl() {
    return confirmationUrl;
  }

  public String getSuccessUrl() {
    return successUrl;
  }
}
