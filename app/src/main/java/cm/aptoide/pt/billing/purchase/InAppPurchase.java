package cm.aptoide.pt.billing.purchase;

public class InAppPurchase extends Purchase {

  private final String signature;
  private final String signatureData;
  private final String sku;

  public InAppPurchase(String productId, String signature, String signatureData, Status status,
      String sku, String transactionId) {
    super(status, productId, transactionId);
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