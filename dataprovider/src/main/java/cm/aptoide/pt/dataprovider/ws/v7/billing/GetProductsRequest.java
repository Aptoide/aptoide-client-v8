/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetProductsRequest
    extends V7<GetProductsRequest.ResponseBody, GetProductsRequest.RequestBody> {

  private GetProductsRequest(RequestBody requestBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(requestBody, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetProductsRequest of(String packageName, List<String> skuList,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    final RequestBody requestBody = new RequestBody();
    requestBody.setPackageName(packageName);
    if (!skuList.isEmpty()) {
      final StringBuilder stringBuilder = new StringBuilder();
      for (String sku : skuList) {
        stringBuilder.append(sku);
        stringBuilder.append(",");
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      requestBody.setSkus(stringBuilder.toString());
    }

    return new GetProductsRequest(requestBody, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static GetProductsRequest of(String packageName, String sku,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setPackageName(packageName);
    requestBody.setSku(sku);
    return new GetProductsRequest(requestBody, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<GetProductsRequest.ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (body.getSkus() != null) {
      return interfaces.getBillingProducts(body, bypassCache);
    }
    return interfaces.getBillingProduct(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private String packageName;
    private String skus;
    private String sku;

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getSkus() {
      return skus;
    }

    public void setSkus(String skus) {
      this.skus = skus;
    }

    public String getSku() {
      return sku;
    }

    public void setSku(String sku) {
      this.sku = sku;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private List<Product> list;
    private Product data;

    public List<Product> getList() {
      return list;
    }

    public void setList(List<Product> list) {
      this.list = list;
    }

    public Product getData() {
      return data;
    }

    public void setData(Product data) {
      this.data = data;
    }

    public static class Product {

      private long id;
      private String sku;
      private String title;
      private String description;
      private String icon;
      private Price price;

      public long getId() {
        return id;
      }

      public void setId(int id) {
        this.id = id;
      }

      public String getSku() {
        return sku;
      }

      public void setSku(String sku) {
        this.sku = sku;
      }

      public String getTitle() {
        return title;
      }

      public void setTitle(String title) {
        this.title = title;
      }

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

      public String getIcon() {
        return icon;
      }

      public void setIcon(String icon) {
        this.icon = icon;
      }

      public Price getPrice() {
        return price;
      }

      public void setPrice(Price price) {
        this.price = price;
      }

      public static class Price {

        private double amount;
        private String currency;
        private String sign;

        public double getAmount() {
          return amount;
        }

        public void setAmount(double amount) {
          this.amount = amount;
        }

        public String getCurrency() {
          return currency;
        }

        public void setCurrency(String currency) {
          this.currency = currency;
        }

        public String getSign() {
          return sign;
        }

        public void setSign(String sign) {
          this.sign = sign;
        }
      }
    }
  }
}
