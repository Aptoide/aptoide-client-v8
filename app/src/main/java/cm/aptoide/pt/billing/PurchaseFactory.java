package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.product.InAppPurchase;
import cm.aptoide.pt.billing.product.SimplePurchase;

public class PurchaseFactory {
  public PurchaseFactory() {
  }

  public InAppPurchase create(String productId, String signature, String signatureData,
      SimplePurchase.Status status, String sku) {
    return new InAppPurchase(productId, signature, signatureData, status, sku);
  }
}