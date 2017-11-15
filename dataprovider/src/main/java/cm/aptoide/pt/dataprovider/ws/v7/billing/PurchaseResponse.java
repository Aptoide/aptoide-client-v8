package cm.aptoide.pt.dataprovider.ws.v7.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class PurchaseResponse {

  private long id;
  private String signature;
  private Product product;
  private Data data;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Product {

    private long id;
    private String sku;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getSku() {
      return sku;
    }

    public void setSku(String sku) {
      this.sku = sku;
    }
  }

  public static class Data {

    private Data.DeveloperPurchase developerPurchase;

    public Data.DeveloperPurchase getDeveloperPurchase() {
      return developerPurchase;
    }

    public void setDeveloperPurchase(Data.DeveloperPurchase developerPurchase) {
      this.developerPurchase = developerPurchase;
    }

    // Order must be kept here because the resulting JSON String must be validated against a digital
    // signature generated and the developer public key.
    @JsonPropertyOrder({
        "orderId", "packageName", "productId", "purchaseTime", "purchaseToken", "purchaseState",
        "developerPayload"
    }) @JsonInclude(JsonInclude.Include.NON_NULL) public static class DeveloperPurchase {
      @JsonProperty("orderId") private String orderId;
      @JsonProperty("packageName") private String packageName;
      @JsonProperty("productId") private String productId;
      @JsonProperty("purchaseTime") private long purchaseTime;
      @JsonProperty("purchaseToken") private String purchaseToken;
      @JsonProperty("purchaseState") private int purchaseState;
      @JsonProperty("developerPayload") private String developerPayload;

      public String getOrderId() {
        return orderId;
      }

      public void setOrderId(String orderId) {
        this.orderId = orderId;
      }

      public String getPackageName() {
        return packageName;
      }

      public void setPackageName(String packageName) {
        this.packageName = packageName;
      }

      public String getProductId() {
        return productId;
      }

      public void setProductId(String productId) {
        this.productId = productId;
      }

      public long getPurchaseTime() {
        return purchaseTime;
      }

      public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
      }

      public String getPurchaseToken() {
        return purchaseToken;
      }

      public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
      }

      public int getPurchaseState() {
        return purchaseState;
      }

      public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
      }

      public String getDeveloperPayload() {
        return developerPayload;
      }

      public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
      }
    }
  }
}
