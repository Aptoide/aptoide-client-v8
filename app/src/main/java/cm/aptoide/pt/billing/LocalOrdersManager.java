package cm.aptoide.pt.billing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neuro on 05-11-2017.
 */

public class LocalOrdersManager {

  private final List<Order> orders;
  private final AtomicInteger atomicInteger;
  private boolean loaded = false;

  public LocalOrdersManager(List<Order> orders, int startIndex) {
    this.orders = orders;
    this.atomicInteger = new AtomicInteger(startIndex);
  }

  public Order getNewOrderId() {
    return orders.get(atomicInteger.getAndIncrement());
  }

  public static class Order {
    private final String signature;
    private final String signatureData;
    private final String purchaseToken;
    private final String productId;
    private final String sku;

    public Order(String signature, String signatureData, String purchaseToken, String productId,
        String sku) {
      this.signature = signature;
      this.signatureData = signatureData;
      this.purchaseToken = purchaseToken;
      this.productId = productId;
      this.sku = sku;
    }

    public String getSignature() {
      return signature;
    }

    public String getSignatureData() {
      return signatureData;
    }

    public String getPurchaseToken() {
      return purchaseToken;
    }

    public String getSku() {
      return sku;
    }
  }
}
