package cm.aptoide.pt.billing.transaction.mol;

import cm.aptoide.pt.billing.transaction.Transaction;

public class MolTransaction extends Transaction {

  private final String confirmationUrl;
  private final String successUrl;

  public MolTransaction(String productId, String customerId, Status status, int paymentMethodId,
      String confirmationUrl, String successUrl, String payload, String merchantName) {
    super(productId, customerId, status, paymentMethodId, payload, merchantName);
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
