package cm.aptoide.pt.billing.product;

public class InAppPurchase extends SimplePurchase {

  private final String signature;
  private final String signatureData;
  private final String sku;
  private final String token;

  public InAppPurchase(long productId, String signature, String signatureData, String sku, String token,
      Status status) {
    super(status, productId);
    this.signature = signature;
    this.signatureData = signatureData;
    this.sku = sku;
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

  public String getToken() {
    return token;
  }
}