package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetTransactionRequest
    extends V7<Response<GetTransactionRequest.ResponseBody>, GetTransactionRequest.RequestBody> {

  private GetTransactionRequest(GetTransactionRequest.RequestBody body, String baseHost,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static GetTransactionRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, long productId) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    return new GetTransactionRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<Response<ResponseBody>> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getBillingTransaction(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private Transaction data;

    public Transaction getData() {
      return data;
    }

    public void setData(Transaction data) {
      this.data = data;
    }

    public static class Transaction {

      private long id;
      private String status;
      private Service service;
      private Product product;
      private User user;

      public long getId() {
        return id;
      }

      public void setId(long id) {
        this.id = id;
      }

      public String getStatus() {
        return status;
      }

      public void setStatus(String status) {
        this.status = status;
      }

      public Service getService() {
        return service;
      }

      public void setService(Service service) {
        this.service = service;
      }

      public Product getProduct() {
        return product;
      }

      public void setProduct(Product product) {
        this.product = product;
      }

      public User getUser() {
        return user;
      }

      public void setUser(User user) {
        this.user = user;
      }

      public static class User {

        private long id;

        public long getId() {
          return id;
        }

        public void setId(long id) {
          this.id = id;
        }
      }

      public static class Service {

        private long id;

        public long getId() {
          return id;
        }

        public void setId(long id) {
          this.id = id;
        }
      }

      public static class Product {

        private long id;

        public long getId() {
          return id;
        }

        public void setId(long id) {
          this.id = id;
        }
      }
    }
  }
}

