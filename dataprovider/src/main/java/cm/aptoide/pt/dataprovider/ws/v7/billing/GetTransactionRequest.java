package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetTransactionRequest extends V7<Response<GetTransactionRequest.ResponseBody>, Void> {

  private final long productId;
  private final String accessToken;

  private GetTransactionRequest(Void body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, long productId, String accessToken) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.productId = productId;
    this.accessToken = accessToken;
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static GetTransactionRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, long productId,
      String accessToken) {
    return new GetTransactionRequest(null, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, productId, accessToken);
  }

  @Override protected Observable<Response<ResponseBody>> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getBillingTransaction(productId, "Bearer " + accessToken);
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

