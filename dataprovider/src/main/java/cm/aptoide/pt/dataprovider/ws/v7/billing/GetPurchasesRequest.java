/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetPurchasesRequest extends
    V7<GetPurchasesRequest.ResponseBody, GetPurchasesRequest.RequestBody> {

  public GetPurchasesRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static GetPurchasesRequest of(String packageName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setPackageName(packageName);
    return new GetPurchasesRequest(body, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getBillingPurchases(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private String packageName;

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private List<Purchase> list;

    public List<Purchase> getList() {
      return list;
    }

    public void setList(List<Purchase> list) {
      this.list = list;
    }

    public static class Purchase {

      private int id;
      private String signature;
      private Product product;
      private Data data;

      public int getId() {
        return id;
      }

      public void setId(int id) {
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

        private int id;
        @JsonProperty("package") private String packageName;
        private String sku;

        public int getId() {
          return id;
        }

        public void setId(int id) {
          this.id = id;
        }

        public String getPackageName() {
          return packageName;
        }

        public void setPackageName(String packageName) {
          this.packageName = packageName;
        }

        public String getSku() {
          return sku;
        }

        public void setSku(String sku) {
          this.sku = sku;
        }
      }

      public static class Data {

        private SignatureData developerData;

        public SignatureData getDeveloperData() {
          return developerData;
        }

        public void setDeveloperData(SignatureData developerData) {
          this.developerData = developerData;
        }

        // Order must be kept here because the resulting JSON String must be validated against a digital
        // signature generated and the developer public key.
        @JsonPropertyOrder({
            "orderId", "packageName", "productId", "purchaseTime", "purchaseToken", "purchaseState",
            "developerPayload"
        }) @JsonInclude(JsonInclude.Include.NON_NULL) public static class SignatureData {
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
  }
}
