package cm.aptoide.pt.billing;

import com.google.gson.Gson;

/**
 * Created by analara on 03/11/2017.
 */

public class PurchaseFile {

  public PurchaseOrderItem[] list;

  public class PurchaseOrderItem

  {
    public long id;
    public String signature;
    public Product product;
    public DataOrder data;
  }

  public class Product {
    public long id;
    public String sku;
  }

  public class data {
    public long id;
    public String sku;
  }

  public class DataOrder {
    public DeveloperPurchase developer_purchase;

    public String toString() {
      return new Gson().toJson(developer_purchase);
    }
  }

  public class DeveloperPurchase {
    public String orderId;
    public String packageName;
    public String productId;
    public long purchaseTime;
    public String purchaseToken;
    public long purchaseState;
  }
}
