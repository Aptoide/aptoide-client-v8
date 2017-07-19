package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class InAppPurchase implements Purchase {

  private final String signature;
  private final int apiVersion;
  private final String signatureData;
  private final String packageName;
  private final String purchaseToken;
  private final String sku;

  public InAppPurchase(int apiVersion, String packageName, String purchaseToken, String signature,
      String signatureData, String sku) {
    this.signature = signature;
    this.apiVersion = apiVersion;
    this.signatureData = signatureData;
    this.packageName = packageName;
    this.purchaseToken = purchaseToken;
    this.sku = sku;
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

  public int getApiVersion() {
    return apiVersion;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getPurchaseToken() {
    return purchaseToken;
  }
}