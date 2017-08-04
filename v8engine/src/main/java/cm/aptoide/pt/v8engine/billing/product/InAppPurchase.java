package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class InAppPurchase implements Purchase {

  private final String signature;
  private final String signatureData;
  private final String sku;
  private final boolean completed;
  private final String token;

  public InAppPurchase(String signature, String signatureData, String sku, boolean completed,
      String token) {
    this.signature = signature;
    this.signatureData = signatureData;
    this.sku = sku;
    this.completed = completed;
    this.token = token;
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

  public String getToken() {
    return token;
  }
}