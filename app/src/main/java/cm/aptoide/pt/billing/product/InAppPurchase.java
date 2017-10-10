package cm.aptoide.pt.billing.product;

public class InAppPurchase extends SimplePurchase {

  private final String signature;
  private final String signatureData;
  private final String sku;

  public InAppPurchase(long productId, String signature, String signatureData, Status status,
      String sku) {
    super(status, productId);
    this.signature = signature;
    this.signatureData = signatureData;
    this.sku = sku;
  }

  public String getSku() {
    return sku;
  }

  public String getSignature() {
    return signature;
  }

  public String getSignatureData() {
    return signatureData;
  }
}