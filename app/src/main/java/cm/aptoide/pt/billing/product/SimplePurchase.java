package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Purchase;

public class SimplePurchase implements Purchase {

  private final Status status;
  private final long productId;

  public SimplePurchase(Status status, long productId) {
    this.status = status;
    this.productId = productId;
  }

  @Override public long getProductId() {
    return productId;
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