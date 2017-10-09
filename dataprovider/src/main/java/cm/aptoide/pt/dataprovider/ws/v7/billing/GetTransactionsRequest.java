package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetTransactionsRequest extends V7<GetTransactionsRequest.ResponseBody, BaseBody> {

  private GetTransactionsRequest(BaseBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static GetTransactionsRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetTransactionsRequest(new BaseBody(), getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getBillingTransaction(body, bypassCache);
  }

  public static class ResponseBody extends BaseV7Response {

    @JsonProperty("datalist") private Data data;

    public Data getData() {
      return data;
    }

    public void setData(Data data) {
      this.data = data;
    }

    public static class Data {

      private List<Transaction> list;

      public List<Transaction> getList() {
        return list;
      }

      public void setList(List<Transaction> list) {
        this.list = list;
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

        public void setId(int id) {
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

          private int id;

          public int getId() {
            return id;
          }

          public void setId(int id) {
            this.id = id;
          }
        }

        public static class Service {

          private int id;

          public int getId() {
            return id;
          }

          public void setId(int id) {
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
}

