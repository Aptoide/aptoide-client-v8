package cm.aptoide.pt.billing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neuro on 05-11-2017.
 */

public class LocalOrdersManager {

  private final List<Order> orders;
  private final AtomicInteger atomicInteger;
  private final SingletonPersister singletonPersister;

  public LocalOrdersManager(List<Order> orders, SingletonPersister singletonPersister) {
    this.orders = orders;
    this.atomicInteger = new AtomicInteger(singletonPersister.load());
    this.singletonPersister = singletonPersister;
  }

  public Order getNewOrderId() {
    Order order = orders.get(atomicInteger.getAndIncrement());
    singletonPersister.persist(atomicInteger.get());
    return order;
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

  public interface SingletonPersister {
    void persist(int integer);

    int load();
  }

}
