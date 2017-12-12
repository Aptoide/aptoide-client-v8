package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.authorization.Authorization;

public class AuthorizedTransaction implements Transaction {

  private final Transaction transaction;
  private final Authorization authorization;

  public AuthorizedTransaction(Transaction transaction, Authorization authorization) {
    this.transaction = transaction;
    this.authorization = authorization;
  }

  public Authorization getAuthorization() {
    return authorization;
  }

  @Override public String getCustomerId() {
    return transaction.getCustomerId();
  }

  @Override public String getProductId() {
    return transaction.getProductId();
  }

  @Override public String getId() {
    return transaction.getId();
  }

  @Override public boolean isNew() {
    return transaction.isNew() && !authorization.isProcessing() && !authorization.isFailed();
  }

  @Override public boolean isCompleted() {
    return transaction.isCompleted();
  }

  @Override public boolean isPendingAuthorization() {
    return transaction.isPendingAuthorization() && authorization.isPending();
  }

  @Override public boolean isProcessing() {
    return transaction.isProcessing() || authorization.isProcessing() || (authorization.isRedeemed()
        && !transaction.isCompleted());
  }

  @Override public boolean isFailed() {
    return transaction.isFailed() || authorization.isFailed();
  }
}
