package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Purchase;

public class InAppPurchase implements Purchase {

  private final String signature;
  private final String signatureData;
  private final String sku;
  private final boolean completed;

  public InAppPurchase(String signature, String signatureData, String sku, boolean completed) {
    this.signature = signature;
    this.signatureData = signatureData;
    this.sku = sku;
    this.completed = completed;
  }

  public String getSignature() {
    return signature;
  }

  public String getSignatureData() {
    return signatureData;
  }

  public String getSku() {
    return sku;
  }

  @Override public boolean isCompleted() {
    return completed;
  }
}