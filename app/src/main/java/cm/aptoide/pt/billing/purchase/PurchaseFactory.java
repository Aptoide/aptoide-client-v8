package cm.aptoide.pt.billing.purchase;

import cm.aptoide.pt.billing.purchase.InAppPurchase;
import cm.aptoide.pt.billing.purchase.PaidAppPurchase;
import cm.aptoide.pt.billing.purchase.Purchase;

public class PurchaseFactory {

  public static final String IN_APP = "IN_APP";
  public static final String PAID_APP = "PAID_APP";

  public Purchase create(String productId, String signature, String signatureData,
      Purchase.Status status, String sku, String type, String apkPath, String transactionId) {

    if (type == null) {
      return new Purchase(status, productId, transactionId);
    }

    switch (type) {
      case IN_APP:
        return new InAppPurchase(productId, signature, signatureData, status, sku, transactionId);
      case PAID_APP:
        return new PaidAppPurchase(apkPath, status, productId, transactionId);
      default:
        return new Purchase(status, productId, transactionId);
    }
  }
}