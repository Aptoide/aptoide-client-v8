package cm.aptoide.pt.v8engine.billing.methods.mol;

import cm.aptoide.pt.v8engine.billing.Transaction;

public class MolTransaction extends Transaction {

  private final String confirmationUrl;
  private final String successUrl;

  public MolTransaction(int productId, String payerId, Status status, int paymentMethodId,
      String confirmationUrl, String successUrl) {
    super(productId, payerId, status, paymentMethodId);
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
