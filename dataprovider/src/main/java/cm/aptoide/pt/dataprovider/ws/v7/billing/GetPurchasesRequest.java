/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetPurchasesRequest
    extends V7<Response<GetPurchasesRequest.ResponseBody>, GetPurchasesRequest.RequestBody> {

  public GetPurchasesRequest(GetPurchasesRequest.RequestBody body, String baseHost,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_READ_V7_HOST
        + "/api/7/";
  }

  public static GetPurchasesRequest of(String merchantName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setPackageName(merchantName);
    return new GetPurchasesRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  public static GetPurchasesRequest of(long productId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    return new GetPurchasesRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<Response<GetPurchasesRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    if (body.getProductId() != 0) {
      return interfaces.getBillingPurchase(body, bypassCache);
    }
    return interfaces.getBillingPurchases(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;
    private String packageName;

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private List<Purchase> list;
    private Purchase data;

    public List<Purchase> getList() {
      return list;
    }

    public void setList(List<Purchase> list) {
      this.list = list;
    }

    public Purchase getData() {
      return data;
    }

    public void setData(Purchase data) {
      this.data = data;
    }

    public static class Purchase {

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

        private DeveloperPurchase developerPurchase;

        public DeveloperPurchase getDeveloperPurchase() {
          return developerPurchase;
        }

        public void setDeveloperPurchase(DeveloperPurchase developerPurchase) {
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
  }
}
