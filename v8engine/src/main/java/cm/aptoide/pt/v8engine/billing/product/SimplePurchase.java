package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class SimplePurchase implements Purchase {

  private final Status status;

  public SimplePurchase(Status status) {
    this.status = status;
  }

  @Override public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  @Override public boolean isPending() {
    return Status.PENDING.equals(status);
  }

  @Override public boolean isFailed() {
    return Status.FAILED.equals(status);
  }

  public static enum Status {
    PENDING, COMPLETED, NEW, FAILED
  }
}