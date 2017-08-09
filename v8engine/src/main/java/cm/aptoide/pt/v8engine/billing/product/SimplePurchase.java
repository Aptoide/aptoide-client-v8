package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class SimplePurchase implements Purchase {

  private final Status status;
  private final String productId;

  public SimplePurchase(Status status, String productId) {
    this.status = status;
    this.productId = productId;
  }

  @Override public String getProductId() {
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